package com.userservice.dto.user;

import com.userservice.entity.user.Role;
import java.util.Set;

public record UpdateRolesRequest(
        Set<Role.RoleName> roles
) {}
