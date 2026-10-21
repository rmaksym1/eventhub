package com.userservice.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRequest(
        @NotBlank(message = "Email cannot be blank!")
        @Email(message = "Invalid email format!")
        @Size(max = 255, message = "Email must not exceed 255 characters!")
        String email,

        @NotBlank(message = "Password cannot be blank!")
        @Size(max = 255, message = "Password must not exceed 255 characters!")
        String password
) {}
