package com.byteblog.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Map;

/**
 * 个人信息 请求类
 */
@Data
public class BlogInfoDto {

    @NotBlank(message = "昵称不能为空")
    @Size(max = 50, message = "昵称不能超过 50 字")
    private String name;
    // 头像
    private String avatarUrl;
    // 个性签名
    private String signature;
    // 关于我
    private String aboutMe;
    // 社交链接
    private Map<String, String> socialLinks;
}
