package com.userservice.dto.user;

import com.userservice.entity.user.Role;
import java.util.List;

public record UserResponse(
        String email,
        String firstName,
        String lastName,
        List<Role> roles
) {}
