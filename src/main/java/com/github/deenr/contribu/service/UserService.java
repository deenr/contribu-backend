package com.github.deenr.contribu.service;

import com.github.deenr.contribu.model.User;

import java.util.Optional;

public interface UserService {
    User save(User user);
    Optional<User> findByEmail(String email);
}
