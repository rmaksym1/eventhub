package com.userservice.service.impl;

import com.eventhub.common.exception.EntityNotFoundException;
import com.userservice.dto.user.*;
import com.userservice.entity.user.Role;
import com.userservice.entity.user.User;
import com.userservice.mapper.UserMapper;
import com.userservice.repository.RoleRepository;
import com.userservice.repository.UserRepository;
import com.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;

    @Override
    public UserResponse getCurrentUser(User user) {
        User actualUser = repository.findByEmail(user.getEmail()).orElseThrow(
                () -> new EntityNotFoundException("Current user not found!")
        );

        return userMapper.toDto(actualUser);
    }

    @Override
    public void editRoles(UpdateRolesRequest request, Long id) {
        User savedUser = repository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("User by id: " + id + " not found!")
        );

        Set<Role> newRoles = request.roles().stream()
                .map(roleName -> roleRepository.findByName(roleName)
                        .orElseThrow(() -> new EntityNotFoundException("Role not found: " + roleName)))
                .collect(Collectors.toSet());

        savedUser.setRoles(newRoles);
        repository.save(savedUser);
    }
}
