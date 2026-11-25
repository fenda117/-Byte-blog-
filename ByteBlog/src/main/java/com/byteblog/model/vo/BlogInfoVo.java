package com.byteblog.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * 个人信息 响应体
 */
@Data
public class BlogInfoVo {
    private String name;

    private String signature;
    private String avatarUrl;
    private String aboutMe;
    private List<SocialLink> socialLinks;

    /**
     * 静态内部类
     */
    @Data
    @AllArgsConstructor
    public static class SocialLink {
        private String label;
        private String value;
    }
}
