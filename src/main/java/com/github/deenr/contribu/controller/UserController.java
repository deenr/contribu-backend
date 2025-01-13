package com.github.deenr.contribu.controller;

import com.github.deenr.contribu.dto.UserAuthenticationDTO;
import com.github.deenr.contribu.dto.UserCreationDTO;
import com.github.deenr.contribu.model.User;
import com.github.deenr.contribu.service.UserService;
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
    public ResponseEntity<User> register(@RequestBody @Valid UserCreationDTO userDTO) {
        return new ResponseEntity<>(userService.register(userDTO.getEmail(), userDTO.getPassword()), HttpStatus.OK);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<Boolean> authenticate(@RequestBody @Valid UserAuthenticationDTO userDTO) {
        return new ResponseEntity<>(userService.authenticate(userDTO.getEmail(), userDTO.getPassword()), HttpStatus.OK);
    }
}
