package com.github.deenr.contribu.controller;

import com.github.deenr.contribu.service.JwtService;
import com.github.deenr.contribu.service.OAuthService;
import com.github.deenr.contribu.service.factory.OAuthServiceFactory;
import com.github.deenr.contribu.util.RefreshTokenUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/oauth")
public class OAuthController {
    private final OAuthServiceFactory oAuthServiceFactory;

    @Autowired
    public OAuthController(OAuthServiceFactory oAuthServiceFactory) {
        this.oAuthServiceFactory = oAuthServiceFactory;
    }

    @GetMapping("/{provider}/authorize")
    public ResponseEntity<String> authorize(@PathVariable String provider) {
        OAuthService oAuthService = oAuthServiceFactory.getOAuthService(provider);
        String authorizationUrl = oAuthService.getAuthorizationUrl();

        return new ResponseEntity<>(authorizationUrl, HttpStatus.OK);
    }

    @PostMapping("/{provider}/callback")
    public ResponseEntity<Object> callback(@PathVariable String provider, @RequestParam("code") String code, HttpServletRequest request) {
        OAuthService oAuthService = oAuthServiceFactory.getOAuthService(provider);

        Optional<Cookie> cookie = RefreshTokenUtil.getRefreshCookie(request.getCookies());
        String refreshToken = cookie.get().getValue();
        String email = JwtService.extractUsername(refreshToken);

        HttpStatusCode statusCode = oAuthService.fetchAndSetToken(code, email);

        return oAuthService.handleCallback(statusCode);
    }
}
