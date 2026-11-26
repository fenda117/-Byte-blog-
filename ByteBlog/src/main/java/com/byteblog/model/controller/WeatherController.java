package com.byteblog.model.controller;

import com.byteblog.common.Result;
import com.byteblog.model.service.WeatherService;
import com.byteblog.model.vo.WeatherVo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 天气控制器。
 *
 * 前端只调这个接口，不直接碰和风天气：
 * 一来 Key 和 API Host 不能出现在浏览器里（前端代码是公开的），
 * 二来同源请求不用配 CORS，三来能在这里做缓存省额度。
 */
@RequiredArgsConstructor
@RequestMapping("/api/weather")
@RestController
public class WeatherController {

    private final WeatherService weatherService;

    /**
     * 当前天气。
     * 未配置或第三方接口异常时 data 为 null，前端据此隐藏卡片，不会报错。
     */
    @GetMapping("/now")
    public Result<WeatherVo> now() {
        return Result.success(weatherService.getNow());
    }
}
