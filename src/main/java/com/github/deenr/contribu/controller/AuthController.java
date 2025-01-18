package com.github.deenr.contribu.controller;

import com.github.deenr.contribu.dto.UserLoginResponseDTO;
import com.github.deenr.contribu.dto.UserRegisterDTO;
import com.github.deenr.contribu.dto.UserLoginDTO;
import com.github.deenr.contribu.dto.UserRegisterResponseDTO;
import com.github.deenr.contribu.model.AccessAndRefreshToken;
import com.github.deenr.contribu.service.JwtService;
import com.github.deenr.contribu.service.AuthService;
import com.github.deenr.contribu.util.RefreshTokenUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserRegisterResponseDTO> register(@RequestBody @Valid UserLoginDTO userDTO, HttpServletResponse response) {
        AccessAndRefreshToken tokens = authService.register(
                userDTO.getFirstName(),
                userDTO.getLastName(),
                userDTO.getEmail(),
                userDTO.getPassword()
        );

        setRefreshTokenCookie(tokens.getRefreshToken(), response);

        return new ResponseEntity<>(new UserRegisterResponseDTO(tokens.getAccessToken()), HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<UserLoginResponseDTO> login(@RequestBody @Valid UserRegisterDTO userDTO, HttpServletResponse response) {
        AccessAndRefreshToken tokens = authService.login(
                userDTO.getEmail(),
                userDTO.getPassword()
        );

        String token = tokens.getRefreshToken();
        setRefreshTokenCookie(token, response);

        return new ResponseEntity<>(new UserLoginResponseDTO(tokens.getAccessToken()), HttpStatus.OK);
    }

    @GetMapping("/refresh")
    public ResponseEntity<String> refresh(HttpServletRequest request) {
        Optional<Cookie> cookie = RefreshTokenUtil.getRefreshCookie(request.getCookies());

        if (cookie.isEmpty()) {
           return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        String refreshToken = cookie.get().getValue();

        if (JwtService.isTokenExpired(refreshToken)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        String email = JwtService.extractUsername(refreshToken);
        return new ResponseEntity<>(JwtService.generateAccessToken(email), HttpStatus.OK);
    }

    void setRefreshTokenCookie(String refreshToken, HttpServletResponse response) {
        Cookie cookie = new Cookie(RefreshTokenUtil.REFRESH_COOKIE, refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setMaxAge((int) (JwtService.REFRESH_TOKEN_EXPIRATION / 1000));
        cookie.setPath("/");

        response.addCookie(cookie);
    }
}
