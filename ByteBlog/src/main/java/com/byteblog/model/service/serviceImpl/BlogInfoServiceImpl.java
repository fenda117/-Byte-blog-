package com.byteblog.model.service.serviceImpl;

import com.byteblog.model.dto.BlogInfoDto;
import com.byteblog.model.entity.BlogInfo;
import com.byteblog.model.mapper.BlogInfoMapper;
import com.byteblog.model.service.BlogInfoService;
import com.byteblog.model.vo.BlogInfoVo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 *  个人信息业务实现层
 */
@Service
@RequiredArgsConstructor
public class BlogInfoServiceImpl implements BlogInfoService {

    private final BlogInfoMapper blogInfoMapper;

    /**
     * 查询个人信息
     * @return
     */
    @Override
    public BlogInfoVo getInfo() {
        BlogInfo info = blogInfoMapper.selectById(1L);
        // 三元运算符 如果个人信息为空就返回新的个人信件界面，不为空就展示个人信息界面
        return info ==null?new BlogInfoVo():toVO(info);
    }

    /**
     * 修改个人信息
     *
     * @param dto
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public BlogInfoVo updateInfo(BlogInfoDto dto) {
        // 查询库，没有就新建
        BlogInfo info = blogInfoMapper.selectById(1L);
        boolean exists = info != null;
        if (!exists) {
            info = new BlogInfo();
        }
        // 复制前端传进来的数据，如果刚开始数据库都无数据，则用到.insert新建个人信息的方法
        BeanUtils.copyProperties(dto, info);
        info.setId(1L);
        if (exists) {
            blogInfoMapper.updateById(info);
        } else {
            blogInfoMapper.insert(info);
        }
        return toVO(blogInfoMapper.selectById(1L));
    }
    // 内部方法，避免过多重复代码
    private BlogInfoVo toVO(BlogInfo info) {
        BlogInfoVo vo = new BlogInfoVo();
        vo.setName(info.getName());
        vo.setSignature(info.getSignature());
        vo.setAvatarUrl(info.getAvatarUrl());
        vo.setAboutMe(info.getAboutMe());
        List<BlogInfoVo.SocialLink> links = new ArrayList<>();
        if (info.getSocialLinks() != null) {
            info.getSocialLinks()
                    .forEach((k, v) -> links.add(new BlogInfoVo.SocialLink(k, v)));
        }
        vo.setSocialLinks(links);
        return vo;
    }
}
