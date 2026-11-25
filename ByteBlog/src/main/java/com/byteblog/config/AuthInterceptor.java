package com.byteblog.config;

import com.byteblog.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 鉴权拦截器：保护所有写请求（POST/PUT/DELETE），公开接口除外
 */
@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String method = request.getMethod();
        // GET 和预检请求直接放行
        if ("GET".equalsIgnoreCase(method) || "OPTIONS".equalsIgnoreCase(method)) {
            return true;
        }
        String uri = request.getRequestURI();
        // 公开的写接口：登录、留言提交、AI 助手对话（访客直接用，不能要 token）
        if (uri.equals("/api/admin/login")
                || uri.equals("/api/messages")
                || uri.startsWith("/api/agent/")) {
            return true;
        }
        // 校验 token
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        String username = token != null ? jwtUtil.parse(token) : null;
        if (username == null) {
            response.setStatus(401);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"msg\":\"未登录或登录已过期\",\"data\":null}");
            return false;
        }
        return true;
    }
}
