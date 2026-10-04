package com.userservice.service;

import com.userservice.dto.user.*;
import com.userservice.entity.user.User;

public interface UserService {
    UserResponse getCurrentUser(User user);

    void editRoles(UpdateRolesRequest request, Long id);
}
