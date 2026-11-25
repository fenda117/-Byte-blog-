package com.byteblog.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Jwt工具类
 * 签发和解析Token
 */
@Component
public class JwtUtil {
    // 签发密钥至少 32字节
    private static final String SECRET = "ByteBlogSecretKeyForJwtSigning20261027_FenDa";
    // 24 小时
    private static final long EXPIRE_MS = 24 * 60 * 60 * 1000L;
    private final SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

    /**
     * 生成Token
     * @param username
     * @return
     */
    public String generate(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRE_MS))
                .signWith(key)
                .compact();
    }
    /**
     * 解析 token，返回用户名；无效或过期返回 null
     */
    public String parse(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();
        } catch (Exception e) {
            return null;
        }
    }
}

