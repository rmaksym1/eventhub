package com.userservice.controller;

import com.userservice.dto.user.AuthResponse;
import com.userservice.dto.user.CreateUserRequest;
import com.userservice.dto.user.UserRequest;
import com.userservice.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Authentication controller", description = "Endpoints for authentication")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    public ResponseEntity<AuthResponse> register(CreateUserRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    @Operation(summary = "Login as existing user")
    public ResponseEntity<AuthResponse> login(UserRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token by refresh token")
    public ResponseEntity<String> refresh(String token) {
        return ResponseEntity.ok(authService.refreshToken(token));
    }
}
