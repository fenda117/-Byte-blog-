package com.byteblog.model.controller;


import com.byteblog.common.Result;
import com.byteblog.model.dto.LogionDto;
import com.byteblog.model.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 后台登录控制器
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admin")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Valid @RequestBody LogionDto dto) {
        String token = authService.login(dto);
        return Result.success("登录成功",Map.of("token", token));
    }
}
