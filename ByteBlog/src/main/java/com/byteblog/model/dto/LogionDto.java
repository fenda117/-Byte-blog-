package com.byteblog.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 后台登录 请求体
 */
@Data
public class LogionDto {

    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;
}
