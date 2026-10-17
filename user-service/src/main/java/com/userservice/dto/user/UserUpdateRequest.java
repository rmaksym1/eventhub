package com.userservice.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(
        @NotBlank(message = "First name cannot be blank!")
        @Size(max = 255, message = "First name cannot exceed 255 characters!")
        String firstName,
        @NotBlank(message = "Last name cannot be blank!")
        @Size(max = 255, message = "Last name cannot exceed 255 characters!")
        String lastName
) {}
