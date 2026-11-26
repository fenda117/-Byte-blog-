package com.byteblog.config;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 * 和风天气配置。
 *
 * 和风天气从 2026 年起停用了 devapi.qweather.com 这类公共地址，
 * 改成「每个账号一个专属 API Host」，形如 abcxyz.qweatherapi.com，
 * 在控制台 - 设置 里能看到。host 和 key 缺一不可，填错了接口会 401。
 *
 * 只要把 weather.enabled 改成 true，再把 host 和 api-key 填上，
 * 侧边栏的天气卡片就会显示实时数据，代码不用动。
 */
@Data
@Component
@ConfigurationProperties(prefix = "weather")
public class WeatherProperties {

    /** 总开关：false 时前端会隐藏天气卡片，也不会发起任何外部请求 */
    private boolean enabled = false;

    /** 专属 API Host，例如 abcxyz.qweatherapi.com，不要带 https:// 和结尾斜杠 */
    private String host = "";

    /** API Key，在控制台创建项目时生成 */
    private String apiKey = "";

    /** 纬度，十进制两位小数，例如北京 39.92 */
    private double latitude = 39.92;

    /** 经度，十进制两位小数，例如北京 116.41 */
    private double longitude = 116.41;

    /** 城市名，只用于卡片上显示，接口本身按经纬度查 */
    private String cityName = "北京";

    /**
     * 缓存分钟数。
     * 免费订阅每天额度有限，而天气本身十分钟内基本不变，
     * 所以在服务端缓存，避免每个访客刷新一次就打一次外部接口。
     */
    private int cacheMinutes = 10;

    /** 单次请求超时（秒），超过就直接放弃，不要让侧边栏卡住 */
    private int timeoutSeconds = 8;

    /**
     * 修正中文乱码。
     *
     * .properties 文件按 Java 规范是以 ISO-8859-1 读取的，
     * 直接写「北京」两个汉字，注入进来会变成「åä¬」这种乱码。
     * 官方做法是写成 \u5317\u4eac 转义，但那样让填配置的人很难受，
     * 所以这里在拿到值之后自己还原一次。
     *
     * 只有一种情况需要还原：字符里既有非 ASCII、又全部落在 Latin-1 范围，
     * 这正是「UTF-8 字节被当成 ISO-8859-1 解读」的特征。
     * 如果将来配置文件改成 UTF-8 读取，汉字会超出 0xFF，这里自动跳过，不会误伤。
     */
    @PostConstruct
    void repairCityName() {
        if (cityName == null || cityName.isEmpty()) {
            return;
        }
        boolean latin1Only = cityName.chars().allMatch(c -> c <= 0xFF);
        boolean hasNonAscii = cityName.chars().anyMatch(c -> c > 0x7F);
        if (latin1Only && hasNonAscii) {
            cityName = new String(cityName.getBytes(StandardCharsets.ISO_8859_1),
                    StandardCharsets.UTF_8);
        }
    }
}
