package com.userservice.service;

import com.userservice.dto.user.AuthResponse;
import com.userservice.dto.user.CreateUserRequest;
import com.userservice.dto.user.UserRequest;

public interface AuthService {
    AuthResponse login(UserRequest request);

    AuthResponse register(CreateUserRequest request);

    String refreshToken(String token);
}
