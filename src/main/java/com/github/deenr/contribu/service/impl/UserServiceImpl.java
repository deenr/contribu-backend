package com.github.deenr.contribu.service.impl;

import com.github.deenr.contribu.exception.EmailAlreadyInUseException;
import com.github.deenr.contribu.model.AccessAndRefreshToken;
import com.github.deenr.contribu.model.User;
import com.github.deenr.contribu.repository.UserRepository;
import com.github.deenr.contribu.service.JwtService;
import com.github.deenr.contribu.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public AccessAndRefreshToken register(String firstName, String lastName, String email, String password) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new EmailAlreadyInUseException("Email is already in use.");
        }

        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setCreatedAt(LocalDateTime.now());

        User savedUser = userRepository.save(user);

        return generateAccessAndRegisterTokens(savedUser.getEmail());
    }

    @Override
    public AccessAndRefreshToken login(String email, String password) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        return generateAccessAndRegisterTokens(user.getEmail());
    }

    private AccessAndRefreshToken generateAccessAndRegisterTokens(String email) {
        String accessToken = JwtService.generateAccessToken(email);
        String refreshToken = JwtService.generateRefreshToken(email);

        return new AccessAndRefreshToken(accessToken, refreshToken);
    }
}
