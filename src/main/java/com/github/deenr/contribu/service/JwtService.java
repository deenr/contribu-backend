package com.github.deenr.contribu.service;

import com.github.deenr.contribu.util.RefreshTokenUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Optional;

@Service
public class JwtService {
    // It's better to load this from configuration/environment variables
    public static final String TOKEN_KEY = "5f8df969eca65228c1808a2565325011a4c1c333f08f02934eba58700a497894";
    public static final long ACCESS_TOKEN_EXPIRATION = 1000 * 60 * 60 * 24; // 1 day
    public static final long REFRESH_TOKEN_EXPIRATION = 1000 * 60 * 60 * 24 * 7; // 7 days

    private static final SecretKey tokenKey = Keys.hmacShaKeyFor(
            TOKEN_KEY.getBytes(StandardCharsets.UTF_8)
    );

    public static String generateAccessToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION))
                .signWith(tokenKey)
                .compact();
    }

    public static String generateRefreshToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION))
                .signWith(tokenKey)
                .compact();
    }

    public static String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public static boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    public static String extractUsernameFromRequest(HttpServletRequest request) {
        Optional<Cookie> cookie = RefreshTokenUtil.getRefreshCookie(request.getCookies());
        if (cookie.isEmpty()) {
            throw new IllegalArgumentException("Missing refresh token");
        }

        try {
            return JwtService.extractUsername(cookie.get().getValue());
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid refresh token", e);
        }
    }

    private static Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(tokenKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
