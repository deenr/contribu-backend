package com.github.deenr.contribu.controller;

import com.github.deenr.contribu.dto.*;
import com.github.deenr.contribu.model.AccessAndRefreshToken;
import com.github.deenr.contribu.model.User;
import com.github.deenr.contribu.service.AuthService;
import com.github.deenr.contribu.service.JwtService;
import com.github.deenr.contribu.service.UserService;
import com.github.deenr.contribu.util.RefreshTokenUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponseDTO> getMyProfile(HttpServletRequest request) {
        String email = JwtService.extractUsernameFromRequest(request);
        User user = userService.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("User not found"));

        UserProfileResponseDTO profile = new UserProfileResponseDTO(
                user.getFirstName(),
                user.getLastName(),
                user.getEmail()
        );

        return ResponseEntity.ok(profile);
    }

    @PutMapping("/me")
    public ResponseEntity<UserProfileResponseDTO> updateMyProfile(@RequestBody @Valid UserProfileDTO userDTO, HttpServletRequest request) {
        String email = JwtService.extractUsernameFromRequest(request);
        User user = userService.updateByEmail(email, userDTO.getFirstName(), userDTO.getLastName());

        UserProfileResponseDTO profile = new UserProfileResponseDTO(
                user.getFirstName(),
                user.getLastName(),
                user.getEmail()
        );

        return ResponseEntity.ok(profile);
    }
}
