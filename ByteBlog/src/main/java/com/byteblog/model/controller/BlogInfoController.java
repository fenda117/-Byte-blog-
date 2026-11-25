package com.byteblog.model.controller;

import com.byteblog.common.Result;
import com.byteblog.model.dto.BlogInfoDto;
import com.byteblog.model.service.BlogInfoService;
import com.byteblog.model.vo.BlogInfoVo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


/**
 个人信息控制器
 实现了@RequiredArgsConstructor注解
 实现了@RestController注解
 是看了@RequestMapping注解
 */
@RestController
@RequestMapping("/api/blog")
@RequiredArgsConstructor
public class BlogInfoController {

    private final BlogInfoService blogInfoService;

    @GetMapping("/info")
    public Result<BlogInfoVo> info() {
        return Result.success(blogInfoService.getInfo());
    }
    @PutMapping("/info")
    public Result<BlogInfoVo> updateInfo(@Valid @RequestBody BlogInfoDto dto) {
        return Result.success(blogInfoService.updateInfo(dto));
    }
}
