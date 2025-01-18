package com.github.deenr.contribu.util;

import jakarta.servlet.http.Cookie;

import java.util.Optional;

public class RefreshTokenUtil {
    public static final String REFRESH_COOKIE = "refresh_token";

    public static Optional<Cookie> getRefreshCookie(Cookie[] cookies) {
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(REFRESH_COOKIE)) {
                return Optional.of(cookie);
            }
        }

        return Optional.empty();
    }
}
