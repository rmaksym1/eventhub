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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;

    @Override
    public UserResponse getCurrentUser(Authentication authentication) {
        User actualUser = repository.findByEmail(authentication.getName()).orElseThrow(
                () -> new EntityNotFoundException("Current user not found!")
        );

        return userMapper.toDto(actualUser);
    }

    @Override
    public void editRoles(UpdateRolesRequest request, Long id) {
        User savedUser = repository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("User by id: " + id + " not found!")
        );

        Set<Role> newRoles = new HashSet<>();

        for (Role.RoleName roleName : request.roles()) {
            Role role = roleRepository.findByName(roleName).orElseThrow(
                    () -> new EntityNotFoundException("Role by name: " + roleName + " not found!")
            );

            newRoles.add(role);
        }

        savedUser.setRoles(newRoles);
        repository.save(savedUser);
    }

    @Override
    public Object getUser(Long id, Authentication authentication) {
        User user = repository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("User by id: " + id + " not found!")
        );

        return containsAdminRole(authentication.getAuthorities())
                ? userMapper.toDto(user)
                : userMapper.toPublicResponse(user);
    }

    @Override
    @Transactional
    public UserResponse updateCurrentUser(Authentication authentication, UserUpdateRequest request) {
        String email = authentication.getName();

        User savedUser = repository.findByEmail(email).orElseThrow(
                () -> new EntityNotFoundException("User by email: " + email + " not found!")
        );

        userMapper.updateUser(request, savedUser);
        return userMapper.toDto(savedUser);
    }

    @Override
    @Transactional
    public UserResponse updateUser(UserUpdateRequest request, Long id) {
        User savedUser = repository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("User by id: " + id + " not found!")
        );

        userMapper.updateUser(request, savedUser);
        return userMapper.toDto(savedUser);
    }

    private boolean containsAdminRole(Collection<? extends GrantedAuthority> roleSet) {
        return roleSet.stream()
                .anyMatch(r -> Objects.equals(r.getAuthority(), Role.RoleName.ROLE_ADMIN.name()));
    }
}
