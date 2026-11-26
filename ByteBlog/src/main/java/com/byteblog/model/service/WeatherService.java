package com.byteblog.model.service;

import com.byteblog.model.vo.WeatherVo;

/**
 * 天气查询。
 */
public interface WeatherService {

    /**
     * 取当前天气。
     *
     * 取不到时返回 null 而不是抛异常：天气只是侧边栏的一个装饰，
     * 第三方接口挂了不该让整页报错。
     *
     * @return 天气数据，未配置或请求失败时返回 null
     */
    WeatherVo getNow();
}
