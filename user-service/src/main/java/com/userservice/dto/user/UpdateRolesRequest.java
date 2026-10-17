package com.userservice.dto.user;

import com.userservice.entity.user.Role;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record UpdateRolesRequest(
        @NotNull(message = "Roles collection cannot be null!")
        @Size(min = 0, max = 10, message = "Roles collection cannot exceed more than 10 roles!")
        Set<Role.RoleName> roles
) {}
