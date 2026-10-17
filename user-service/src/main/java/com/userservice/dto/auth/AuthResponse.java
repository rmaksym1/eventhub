package com.userservice.dto.auth;

public record AuthResponse(
        String activeToken,
        String refreshToken
) {}
