package com.userservice.service;

import com.userservice.dto.user.*;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;

public interface UserService {
    UserResponse getCurrentUser(Authentication authentication);

    void editRoles(UpdateRolesRequest request, Long id);

    Object getUser(Long id, Authentication authentication);

    UserResponse updateCurrentUser(Authentication authentication, UserUpdateRequest request);

    UserResponse updateUser(UserUpdateRequest request, Long id);
}
