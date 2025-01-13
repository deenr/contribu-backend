package com.github.deenr.contribu.service;

import com.github.deenr.contribu.model.User;

public interface UserService {
    User register(String email, String password);
    boolean authenticate(String email, String password);
}
