package com.github.deenr.contribu.controller;

import com.github.deenr.contribu.exception.ProviderNotSupportedException;
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

    @ExceptionHandler(ProviderNotSupportedException.class)
    public ResponseEntity<String> handleProviderNotSupportedException(ProviderNotSupportedException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @GetMapping("/{provider}/authorize")
    public ResponseEntity<String> authorize(@PathVariable String provider) {
        OAuthService oAuthService = oAuthServiceFactory.getOAuthService(provider);
        String authorizationUrl = oAuthService.getAuthorizationUrl();

        return new ResponseEntity<>(authorizationUrl, HttpStatus.OK);
    }

    @PostMapping("/{provider}/deauthorize")
    public ResponseEntity<?> deauthorize(@PathVariable String provider, HttpServletRequest request) {
        try {
            OAuthService oAuthService = oAuthServiceFactory.getOAuthService(provider);

            if (request.getCookies() == null) {
                return ResponseEntity.badRequest().body("No cookies found in the request.");
            }

            Optional<Cookie> cookie = RefreshTokenUtil.getRefreshCookie(request.getCookies());
            if (cookie.isEmpty()) {
                return ResponseEntity.badRequest().body("Refresh token cookie not found.");
            }

            String refreshToken = cookie.get().getValue();
            String email = JwtService.extractUsername(refreshToken);

            if (email == null || email.isEmpty()) {
                return ResponseEntity.badRequest().body("Invalid refresh token.");
            }

            oAuthService.removeToken(email);

            return ResponseEntity.ok("Deauthorization successful.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid provider: " + provider);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while processing the deauthorization request.");
        }
    }

    @PostMapping("/{provider}/callback")
    public ResponseEntity<Object> callback(@PathVariable String provider, @RequestParam("code") String code, HttpServletRequest request) {
        System.err.println(provider);
        OAuthService oAuthService = oAuthServiceFactory.getOAuthService(provider);

        Optional<Cookie> cookie = RefreshTokenUtil.getRefreshCookie(request.getCookies());
        String refreshToken = cookie.get().getValue();
        String email = JwtService.extractUsername(refreshToken);
        HttpStatusCode statusCode = oAuthService.fetchAndSetToken(code, email);

        return oAuthService.handleCallback(statusCode);
    }
}
