package com.byteblog.model.service.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.byteblog.common.ResultCode;
import com.byteblog.exception.BusinessException;
import com.byteblog.model.dto.MessageDTO;
import com.byteblog.model.entity.Message;
import com.byteblog.model.mapper.MessageMapper;
import com.byteblog.model.service.MessageService;
import com.byteblog.model.service.MessageAuditService;
import com.byteblog.model.vo.MessageVo;
import com.byteblog.util.IpLocationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 留言板业务实现类
 */
@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    //注入mapper
    private final MessageMapper messageMapper;
    private final IpLocationUtils ipLocationUtils;
    private final MessageAuditService messageAuditService;

    /**
     * 创建留言的方法
     * 做到了安全校验，头像处理,ip地址查询
     * @param dto
     * @param ip
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertMessage(MessageDTO dto, String ip) {
        Message message = new Message();
        BeanUtils.copyProperties(dto, message);
        // 状态 待审核
        message.setStatus(0);
        // 默认不置顶
        message.setIsTop(false);
        // 创建留言的时间
        message.setCreateTime(LocalDateTime.now());
        // 安全校验:如果是回复留言，后端自己去查父留言名字
        if(dto.getParentId() != null&&dto.getParentId() != 0L){
            Message parentMsg = messageMapper.selectById(dto.getParentId());
            if(parentMsg==null){
                    throw new BusinessException(ResultCode.MESSAGE_NOT_FOUND);
            }
            message.setReplyName(parentMsg.getReplyName());
        }else {
            // 顶级留言
            message.setParentId(0L);
            // 顶级留言没有回复对象
            message.setReplyName(null);
        }
        // 头像处理
        if(message.getAvatar()==null|| message.getAvatar().trim().isEmpty()){
            // 如果不传头像或头像为空，自动生成一个极简风卡通头像（调用免费api地址）
            message.setAvatar("https://api.dicebear.com/7.x/notionists/svg?seed=" + dto.getName());
        }
        // ip处理
        String location = ipLocationUtils.getRealLocation(ip);
        message.setIpLocation(location);
        // 创建留言
        messageMapper.insert(message);

        // 交给 AI 先审一道。
        // 必须等事务提交后再触发——否则异步线程开的是另一个连接，
        // 查不到这条还没提交的记录，会当成"留言不存在"直接跳过。
        Long newId = message.getId();
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    messageAuditService.auditAsync(newId);
                }
            });
        } else {
            messageAuditService.auditAsync(newId);
        }
    }

    /**
     * 留言板展示
     * 用到组装树形结构（二级盖楼）
     * @return
     */
    @Override
    public List<MessageVo> listMessages() {
        // 置顶在前，按时间倒序排序
        List<Message> allMessages = messageMapper.selectList(new LambdaQueryWrapper<Message>()
                .eq(Message::getStatus, 1)
                .orderByDesc(Message::getIsTop)
                .orderByDesc(Message::getCreateTime)
        );
        List<MessageVo> allVos = new ArrayList<>();
        for (Message msg : allMessages) {
            allVos.add(toVo(msg));
        }
        List<MessageVo> rootMessages = new ArrayList<>();
        for (MessageVo msg : allVos) {
            if(msg.getParentId()==null||msg.getParentId()==0L){
                // 顶层留言
                rootMessages.add(msg);
                // 子留言：找到父留言
            }else {
                for (MessageVo parent : allVos) {
                    if (parent.getId().equals(msg.getParentId())) {
                        if (parent.getChildren() == null) {
                            parent.setChildren(new ArrayList<>());
                        }
                        parent.getChildren().add(msg);
                        break;
                    }
                }
            }
        }
        return rootMessages;
    }

    /**
     * 后台管理：全部留言（含待审核），平铺返回，方便审核操作
     *
     * 排序刻意用 CASE：把「AI 拿不准、还没人处理」的排最前面，
     * 站长打开后台第一眼看到的就是需要他决定的，不用自己翻。
     * 注意要带 status=0 —— 人工处理过的虽然 audit_status 还是 3，但已经有结论了，不该再排前面。
     */
    @Override
    public List<MessageVo> listAllMessages() {
        List<Message> all = messageMapper.selectList(new LambdaQueryWrapper<Message>()
                .last("ORDER BY CASE WHEN audit_status = 3 AND status = 0 THEN 0 "
                        + "WHEN audit_status = 0 THEN 1 ELSE 2 END, create_time DESC"));
        List<MessageVo> vos = new ArrayList<>();
        for (Message msg : all) {
            vos.add(toVo(msg));
        }
        return vos;
    }

    /**
     * 只取「AI 判不了、还没人处理」的那些。
     *
     * 条件必须同时满足 audit_status=3 和 status=0：
     * 人工处理完之后 audit_status 仍保留 3（记录 AI 当时的判断），
     * 只按 audit_status 查的话，处理完的还会赖在列表里。
     */
    @Override
    public List<MessageVo> listPendingMessages() {
        List<Message> all = messageMapper.selectList(new LambdaQueryWrapper<Message>()
                .eq(Message::getAuditStatus, 3)
                .eq(Message::getStatus, 0)
                .orderByDesc(Message::getCreateTime));
        List<MessageVo> vos = new ArrayList<>();
        for (Message msg : all) {
            vos.add(toVo(msg));
        }
        return vos;
    }

    /**
     * 置顶留言的方法
     * @param id
     * @param isTop
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void topMessage(Long id, Boolean isTop) {
        Message message = messageMapper.selectById(id);
        if(message==null){
            throw new BusinessException(ResultCode.MESSAGE_NOT_FOUND);
        }
        message.setIsTop(isTop);
        messageMapper.updateById(message);
    }

    /**
     * 人工审核留言。
     *
     * status: 0待审核 1通过 2拒绝
     * 除了改对外状态，还要把 audit_by 标成 manual——
     * 这样后台能看出「这条是 AI 判的」还是「AI 拿不准、人拍的板」。
     * audit_status 保持原值不动，因为它记录的是 AI 当时的判断，不该被覆盖。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void auditMessage(Long id, Integer status) {
        Message message = messageMapper.selectById(id);
        if(message==null){
            throw new BusinessException(ResultCode.MESSAGE_NOT_FOUND);
        }
        message.setStatus(status);
        message.setAuditBy("manual");
        message.setAuditTime(LocalDateTime.now());
        messageMapper.updateById(message);
    }

    /**
     * 删除留言
     * 这里删除父留言的话及其子留言也一并删除
     * @param id
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMessage(Long id) {
        int message = messageMapper.deleteById(id);
        if(message==0){
            throw new BusinessException(ResultCode.MESSAGE_NOT_FOUND);
        }
        messageMapper.delete(new LambdaQueryWrapper<Message>().eq(Message::getParentId,id));
    }
    // 内部方法 避免过多重复代码
    private MessageVo toVo(Message msg) {
        MessageVo vo = new MessageVo();
        BeanUtils.copyProperties(msg, vo);
        return vo;
    }

}
