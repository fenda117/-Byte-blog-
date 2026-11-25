package com.byteblog.config;

import com.byteblog.util.IpLocationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.LocalDate;

/**
 * 访问拦截器配置类
 */
@Component
@RequiredArgsConstructor
public class VisitInterceptorConfig implements HandlerInterceptor {

    private final StringRedisTemplate redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String uri = request.getRequestURI();

        // 只统计具体的业务接口，过滤掉 Swagger 文档和错误页，防止疯狂刷数据
        if (uri.startsWith("/api/") && !uri.contains("/doc") && !uri.contains("/webjars") && !uri.contains("/stats")) {
            String ip = IpLocationUtils.getRealIp();
            String today = LocalDate.now().toString();
            // 记录 PV（每次访问 +1）
            redisTemplate.opsForValue().increment("visit:pv:" + today);

            // 记录 UV
            redisTemplate.opsForSet().add("visit:uv:" + today, ip);
        }
        return true;
    }
}
