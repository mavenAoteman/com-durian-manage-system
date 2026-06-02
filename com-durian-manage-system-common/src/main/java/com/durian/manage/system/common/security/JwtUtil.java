package com.durian.manage.system.common.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT 工具：HS256 签发 / 解析。
 * secret 长度需 >= 32 字节（256 bit），生产环境务必通过 application.properties 覆盖。
 */
@Component
public class JwtUtil {

    @Value("${jwt.secret:durian-soap-default-secret-key-please-change-in-production-32bytes-min}")
    private String secret;

    @Value("${jwt.expire-seconds:7200}")
    private long expireSeconds;

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String issue(Long userId, String username, Integer role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("uid", userId);
        claims.put("name", username);
        claims.put("role", role);
        Date now = new Date();
        Date exp = new Date(now.getTime() + expireSeconds * 1000L);
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public UserContext parse(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        Long uid = claims.get("uid", Number.class).longValue();
        String name = claims.get("name", String.class);
        Integer role = claims.get("role", Number.class).intValue();
        return new UserContext(uid, name, role);
    }

    public long getExpireSeconds() {
        return expireSeconds;
    }
}
