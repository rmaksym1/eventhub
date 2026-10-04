package com.userservice.dto.user;

import com.userservice.validation.FieldMatch;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@FieldMatch(field = "password",
        fieldToMatch = "repeatPassword", message = "Password and repeat password doesn't match!")
public record CreateUserRequest(
        @NotBlank(message = "Email cannot be null!")
        @Size(max = 255, message = "Email must be between 0 and 255 characters long!")
        String email,
        @NotBlank(message = "First name cannot be null!")
        @Size(max = 255, message = "First name must be between 0 and 255 characters long!")
        String firstName,
        @Size(max = 255, message = "Email must not be longer than 255 characters!")
        String lastName,
        @NotBlank(message = "Password cannot be null!")
        @Size(max = 255, message = "Password must be between 0 and 255 characters long!")
        String password,
        @NotBlank(message = "Repeat password cannot be null!")
        @Size(max = 255, message = "Repeat password must be between 0 and 255 characters long!")
        String repeatPassword
) {}
