package com.byteblog.model.service.serviceImpl;

import com.byteblog.config.WeatherProperties;
import com.byteblog.model.service.WeatherService;
import com.byteblog.model.vo.WeatherVo;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.zip.GZIPInputStream;

/**
 * 和风天气实现。
 *
 * 接口是 2026 年改版后的 v1：
 *   GET https://{你的专属host}/weather/v1/current/{纬度}/{经度}
 *   认证走请求头 X-QW-Api-Key，不再用 URL 上的 key= 参数
 *
 * 两个容易踩的点，都在这处理了：
 *   ① 和风所有响应都强制 gzip，Java 的 HttpClient 不会自动解压，
 *      直接把字节转成字符串会得到一堆乱码，必须手动解压
 *   ② 免费额度按次计，天气十分钟内基本不变，所以在服务端缓存，
 *      不能让每个访客刷新页面就打一次外部接口
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WeatherServiceImpl implements WeatherService {

    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("MM-dd HH:mm");

    /** 和风的方位代码，顺序与下面的中文一一对应 */
    private static final String[] COMPASS = {
            "n", "nne", "ne", "ene", "e", "ese", "se", "sse",
            "s", "ssw", "sw", "wsw", "w", "wnw", "nw", "nnw"
    };
    private static final String[] COMPASS_CN = {
            "北", "北偏东", "东北", "东偏北", "东", "东偏南", "东南", "南偏东",
            "南", "南偏西", "西南", "西偏南", "西", "西偏北", "西北", "北偏西"
    };

    private final WeatherProperties props;
    private final ObjectMapper objectMapper;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    /** 缓存值与写入时间，volatile 保证多线程下读到的是一对完整数据 */
    private volatile WeatherVo cache;
    private volatile long cacheAt = 0L;

    private final Object lock = new Object();

    @Override
    public WeatherVo getNow() {
        if (!props.isEnabled() || blank(props.getHost()) || blank(props.getApiKey())) {
            return null;
        }

        long ttl = Math.max(1, props.getCacheMinutes()) * 60_000L;
        WeatherVo snapshot = cache;
        if (snapshot != null && System.currentTimeMillis() - cacheAt < ttl) {
            return snapshot;
        }

        synchronized (lock) {
            // 双重检查：等锁期间别人可能已经刷新过了
            if (cache != null && System.currentTimeMillis() - cacheAt < ttl) {
                return cache;
            }
            WeatherVo fresh = fetch();
            if (fresh != null) {
                cache = fresh;
                cacheAt = System.currentTimeMillis();
                return fresh;
            }
            // 拉取失败时，如果手里还有旧数据就继续用，总比卡片突然空掉好
            return cache;
        }
    }

    private WeatherVo fetch() {
        String url = buildUrl();
        HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                .timeout(Duration.ofSeconds(Math.max(3, props.getTimeoutSeconds())))
                .header("X-QW-Api-Key", props.getApiKey().trim())
                .header("Accept-Encoding", "gzip")
                .GET()
                .build();

        try {
            HttpResponse<byte[]> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());

            if (response.statusCode() != 200) {
                // 401 就是 host 或 key 不对，403 多半是订阅过期或接口不在套餐内
                log.warn("天气接口返回 {}，请检查 weather.host / weather.api-key 是否正确", response.statusCode());
                return null;
            }

            String body = ungzip(response.body());
            return parse(body);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("天气请求被中断");
            return null;
        } catch (Exception e) {
            log.warn("天气请求失败：{}", e.getMessage());
            return null;
        }
    }

    private String buildUrl() {
        // 允许只写域名，也容忍手滑把 https:// 一起粘进来
        String host = props.getHost().trim().replaceAll("/+$", "");
        String base = host.startsWith("http://") || host.startsWith("https://")
                ? host
                : "https://" + host;
        return String.format("%s/weather/v1/current/%.2f/%.2f?localTime=true",
                base, props.getLatitude(), props.getLongitude());
    }

    /**
     * 和风固定返回 gzip。判断魔数而不是看响应头，
     * 因为有些代理会把 Content-Encoding 头剥掉，但字节仍是压缩过的。
     */
    private String ungzip(byte[] raw) throws IOException {
        if (raw.length > 2 && (raw[0] & 0xFF) == 0x1F && (raw[1] & 0xFF) == 0x8B) {
            try (GZIPInputStream in = new GZIPInputStream(new ByteArrayInputStream(raw))) {
                return new String(in.readAllBytes(), StandardCharsets.UTF_8);
            }
        }
        return new String(raw, StandardCharsets.UTF_8);
    }

    private WeatherVo parse(String body) throws IOException {
        JsonNode root = objectMapper.readTree(body);

        // 出错时返回体里没有 temperature 字段，这一条能同时兜住结构变更和业务错误
        if (!root.has("temperature")) {
            log.warn("天气接口返回了非预期结构：{}", abbreviate(body));
            return null;
        }

        String code = root.path("condition").path("code").asText("");
        // 湿度原始值是 0-1 的小数，换算成百分数并四舍五入
        double humidity = root.path("humidity").asDouble(0);

        return WeatherVo.builder()
                .city(props.getCityName())
                .text(root.path("condition").path("text").asText(""))
                .code(code)
                .icon(iconOf(code))
                .temperature((int) Math.round(root.path("temperature").path("value").asDouble()))
                .feelsLike((int) Math.round(root.path("feelsLike").path("value").asDouble()))
                .humidity((int) Math.round(humidity * 100))
                .windDir(windDirOf(root.path("wind").path("direction").path("compass").asText("")))
                .windScale(root.path("wind").path("scale").asInt(0))
                .updateTime(formatTime(root.path("validTime").asText("")))
                .build();
    }

    /**
     * 把和风的天气现象代码归成七类，前端只需要七套图标。
     *
     * 编号规则：1xx 晴到阴、3xx 雨、4xx 雪、5xx 雾霾沙尘，2xx 是各种-11 的变体。
     */
    private String iconOf(String code) {
        int c;
        try {
            c = Integer.parseInt(code);
        } catch (NumberFormatException e) {
            return "cloudy";
        }

        if (c == 100 || c == 150) {
            return "clear";
        }
        if (c == 104) {
            return "cloudy";
        }
        if (c >= 101 && c <= 103 || c >= 151 && c <= 153) {
            return "partly";
        }
        if (c >= 300 && c <= 399) {
            // 302 雷阵雨、303 强雷阵雨、304 雷阵雨伴有冰雹
            return (c >= 302 && c <= 304) ? "thunder" : "rain";
        }
        if (c >= 400 && c <= 499) {
            return "snow";
        }
        if (c >= 500 && c <= 515) {
            return "fog";
        }
        return "cloudy";
    }

    private String windDirOf(String compass) {
        if (blank(compass)) {
            return "";
        }
        String key = compass.trim().toLowerCase();
        if ("vrb".equals(key)) {
            return "风向不定";
        }
        for (int i = 0; i < COMPASS.length; i++) {
            if (COMPASS[i].equals(key)) {
                return COMPASS_CN[i];
            }
        }
        return "";
    }

    /**
     * validTime 是 ISO 格式，例如 2024-05-31T03:15Z 或 2024-05-31T11:15+08:00。
     * 用了 localTime=true，接口已经按当地时间返回，这里只做格式美化，不再转时区。
     */
    private String formatTime(String iso) {
        if (blank(iso)) {
            return "";
        }
        try {
            return OffsetDateTime.parse(iso).format(TIME_FMT);
        } catch (Exception e) {
            return iso;
        }
    }

    private boolean blank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private String abbreviate(String s) {
        if (s == null) {
            return "";
        }
        return s.length() <= 200 ? s : s.substring(0, 200) + "...";
    }
}
