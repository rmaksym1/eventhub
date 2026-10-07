package com.userservice.dto.user;

public record AuthResponse(
        String activeToken,
        String refreshToken
) {}
