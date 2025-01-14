package com.github.deenr.contribu.model;

public class AccessAndRefreshToken {
    private String accessToken;
    private String refreshToken;

    public AccessAndRefreshToken(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }
}
