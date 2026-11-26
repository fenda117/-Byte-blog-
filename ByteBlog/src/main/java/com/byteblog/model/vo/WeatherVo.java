package com.byteblog.model.vo;

import lombok.Builder;
import lombok.Data;

/**
 * 天气卡片数据。
 *
 * 只挑侧边栏用得到的字段，不把和风的原始结构整个透出去——
 * 第三方接口的字段随时可能变，前端依赖我们自己的 VO 更稳。
 */
@Data
@Builder
public class WeatherVo {

    /** 城市名，来自配置，不来自接口 */
    private String city;

    /** 天气现象文字，例如「少云」「小雨」 */
    private String text;

    /** 和风的天气现象代码，例如 100、305 */
    private String code;

    /**
     * 归一化后的图标类别，前端按它选 SVG：
     * clear 晴 / partly 多云 / cloudy 阴 / rain 雨 / thunder 雷 / snow 雪 / fog 雾霾
     */
    private String icon;

    /** 温度，摄氏度，已四舍五入成整数 */
    private Integer temperature;

    /** 体感温度 */
    private Integer feelsLike;

    /** 相对湿度，百分数 0-100（接口原始值是 0-1 的小数，这里换算过） */
    private Integer humidity;

    /** 风向中文，例如「西南」 */
    private String windDir;

    /** 风力等级（蒲福风级） */
    private Integer windScale;

    /** 数据时间，格式 MM-dd HH:mm */
    private String updateTime;
}
