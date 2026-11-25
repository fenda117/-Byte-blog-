package com.byteblog.model.controller;

import com.byteblog.common.Result;
import com.byteblog.model.mapper.SiteStatMapper;
import com.byteblog.model.service.SiteStatService;
import com.byteblog.model.vo.SiteStatVo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 统计网站控制器
 */
@RequiredArgsConstructor
@RequestMapping("/api/stats")
@RestController
public class SiteStatController {

    private final SiteStatService siteStatService;

    @GetMapping
    public Result<SiteStatVo> getStats() {
        return Result.success(siteStatService.getSiteStats());
    }

}
