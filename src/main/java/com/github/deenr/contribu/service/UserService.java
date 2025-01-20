package com.github.deenr.contribu.service;

import com.github.deenr.contribu.model.User;

import java.time.LocalDateTime;
import java.util.Optional;

public interface UserService {
    User save(String firstName, String lastName, String email, String password, LocalDateTime createdAt);
    Optional<User> findByEmail(String email);
    User updateByEmail(String email, String firstName, String lastName);
}
