package com.userservice.service;

import com.userservice.dto.auth.AuthResponse;
import com.userservice.dto.auth.CreateUserRequest;
import com.userservice.dto.user.UserRequest;

public interface AuthService {
    AuthResponse login(UserRequest request);

    AuthResponse register(CreateUserRequest request);

    String refreshToken(String token);
}
