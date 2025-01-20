package com.github.deenr.contribu.service;

import com.github.deenr.contribu.enums.TokenStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;



public interface OAuthService {
    String getAuthorizationUrl();
    HttpStatusCode fetchAndSetToken(String code, String email);
    ResponseEntity<Object> handleCallback(HttpStatusCode statusCode);
    void removeToken(String email);
    TokenStatus getTokenStatus(String token);
}
