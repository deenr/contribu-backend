package com.github.deenr.contribu.service;

import com.github.deenr.contribu.model.User;

import java.util.Optional;

public interface UserService {
    String register(String firstName, String lastName, String email, String password);
    String login(String email, String password);
}
