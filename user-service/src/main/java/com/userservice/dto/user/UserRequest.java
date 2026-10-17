package com.userservice.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRequest(
        @NotBlank(message = "Email cannot be null!")
        @Size(max = 255, message = "Email must be between 0 and 255 characters long!")
        String email,
        @NotBlank(message = "Password cannot be null!")
        @Size(max = 255, message = "Password must be between 0 and 255 characters long!")
        String password
) {}
