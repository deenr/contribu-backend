package com.github.deenr.contribu.service;

import com.github.deenr.contribu.model.AccessAndRefreshToken;

public interface UserService {
    AccessAndRefreshToken register(String firstName, String lastName, String email, String password);
    AccessAndRefreshToken login(String email, String password);
}
