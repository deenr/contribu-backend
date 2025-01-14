package com.github.deenr.contribu.controller;

import com.github.deenr.contribu.dto.UserLoginResponseDTO;
import com.github.deenr.contribu.dto.UserRegisterDTO;
import com.github.deenr.contribu.dto.UserLoginDTO;
import com.github.deenr.contribu.dto.UserRegisterResponseDTO;
import com.github.deenr.contribu.model.AccessAndRefreshToken;
import com.github.deenr.contribu.service.JwtService;
import com.github.deenr.contribu.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserRegisterResponseDTO> register(@RequestBody @Valid UserLoginDTO userDTO, HttpServletResponse response) {
        AccessAndRefreshToken tokens = userService.register(
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
        AccessAndRefreshToken tokens = userService.login(
                userDTO.getEmail(),
                userDTO.getPassword()
        );

        setRefreshTokenCookie(tokens.getRefreshToken(), response);

        return new ResponseEntity<>(new UserLoginResponseDTO(tokens.getAccessToken()), HttpStatus.OK);
    }

    void setRefreshTokenCookie(String refreshToken, HttpServletResponse response) {
        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(Long.valueOf(JwtService.REFRESH_TOKEN_EXPIRATION).intValue());
        response.addCookie(cookie);
    }
}
