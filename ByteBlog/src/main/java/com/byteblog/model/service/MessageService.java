package com.byteblog.model.service;

import com.byteblog.model.dto.MessageDTO;
import com.byteblog.model.vo.MessageVo;

import java.util.List;

/**
 * 留言板业务接口
 */
public interface MessageService {
    /**
     * 新建留言
     * @param dto
     * @param ip
     */
    void insertMessage(MessageDTO dto, String ip);

    /**
     * 留言板
     * @return
     */
    List<MessageVo> listMessages();

    /**
     * 后台管理用：返回全部留言（含待审核、已拒绝），平铺不分树
     * @return
     */
    List<MessageVo> listAllMessages();

    /**
     * 只返回「AI 判不了、等人工」的留言
     * @return
     */
    List<MessageVo> listPendingMessages();

    /**
     * 置顶留言
     * @param id
     * @param isTop
     */
    void topMessage(Long id, Boolean isTop);

    /**
     * 人工审核留言
     * @param id 留言 id
     * @param status 0待审核 1通过 2拒绝
     */
    void auditMessage(Long id, Integer status);

    /**
     * 删除留言
     * @param id
     */
    void deleteMessage(Long id);

}
