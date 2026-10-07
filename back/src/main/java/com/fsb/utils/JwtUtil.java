package com.fsb.utils;

import com.fsb.auth.AuthUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class JwtUtil {

    @Value("${jwt.secret:}")
    private String secret;

    @Value("${jwt.expiration-millis:604800000}")
    private long expirationMillis;

    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        if (secret == null || secret.length() < 32) {
            throw new IllegalStateException("JWT_SECRET must be configured with at least 32 characters");
        }
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(AuthUser authUser) {
        long now = System.currentTimeMillis();
        Date issuedAt = new Date(now);
        Date expiresAt = new Date(now + expirationMillis);

        return Jwts.builder()
                .subject(authUser.getUsername())
                .claim("uid", authUser.getUserId())
                .claim("role", authUser.getRole())
                .claim("permissions", authUser.getPermissions())
                .issuedAt(issuedAt)
                .expiration(expiresAt)
                .signWith(secretKey)
                .compact();
    }

    @SuppressWarnings("unchecked")
    public AuthUser parseToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        Long uid = claims.get("uid", Long.class);
        String role = claims.get("role", String.class);
        String username = claims.getSubject();
        List<String> permissions = claims.get("permissions", List.class);
        if (permissions == null) {
            permissions = new ArrayList<>();
        }

        return AuthUser.builder()
                .userId(uid)
                .username(username)
                .role(role)
                .permissions(permissions)
                .build();
    }
}
