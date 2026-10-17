package com.userservice.dto.user;

public record PublicUserResponse(
        Long id,
        String email,
        String lastName
) {}
