package com.userservice.service;

import com.eventhub.common.exception.EntityNotFoundException;
import com.userservice.dto.user.PublicUserResponse;
import com.userservice.dto.user.UpdateRolesRequest;
import com.userservice.dto.user.UserResponse;
import com.userservice.dto.user.UserUpdateRequest;
import com.userservice.entity.user.Role;
import com.userservice.entity.user.User;
import com.userservice.mapper.UserMapper;
import com.userservice.repository.RoleRepository;
import com.userservice.repository.UserRepository;
import com.userservice.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository repository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private Authentication userAuth;
    private Authentication adminAuth;

    @BeforeEach
    void setUp() {
        userAuth = authentication("user@test.com", "ROLE_USER");
        adminAuth = authentication("admin@test.com", "ROLE_ADMIN");
    }

    @Test
    @DisplayName("Should return the current user")
    void shouldGetCurrentUser() {
        User user = user("user@test.com");
        UserResponse expected = userResponse();

        when(repository.findByEmail("user@test.com")).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(expected);

        UserResponse response = userService.getCurrentUser(userAuth);

        assertThat(response).isEqualTo(expected);
    }

    @Test
    @DisplayName("Should throw when the current user does not exist")
    void shouldThrowWhenCurrentUserDoesNotExist() {
        when(repository.findByEmail("user@test.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getCurrentUser(userAuth))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Current user not found!");
    }

    @Test
    @DisplayName("Should replace user roles and save the user")
    void shouldEditRoles() {
        User user = user("user@test.com");
        Role adminRole = role(Role.RoleName.ROLE_ADMIN);
        Role userRole = role(Role.RoleName.ROLE_USER);
        UpdateRolesRequest request = new UpdateRolesRequest(Set.of(Role.RoleName.ROLE_ADMIN, Role.RoleName.ROLE_USER));

        when(repository.findById(3L)).thenReturn(Optional.of(user));
        when(roleRepository.findByName(Role.RoleName.ROLE_ADMIN)).thenReturn(Optional.of(adminRole));
        when(roleRepository.findByName(Role.RoleName.ROLE_USER)).thenReturn(Optional.of(userRole));
        when(repository.save(user)).thenReturn(user);

        userService.editRoles(request, 3L);

        assertThat(user.getRoles()).containsExactlyInAnyOrder(adminRole, userRole);
        verify(repository).save(user);
    }

    @Test
    @DisplayName("Should throw when editing roles of a missing user")
    void shouldThrowWhenEditingRolesOfMissingUser() {
        UpdateRolesRequest request = new UpdateRolesRequest(Set.of(Role.RoleName.ROLE_USER));
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.editRoles(request, 99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("User by id: 99");

        verify(repository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    @DisplayName("Should throw when a requested role does not exist")
    void shouldThrowWhenRoleDoesNotExist() {
        User user = user("user@test.com");
        UpdateRolesRequest request = new UpdateRolesRequest(Set.of(Role.RoleName.ROLE_ADMIN));

        when(repository.findById(3L)).thenReturn(Optional.of(user));
        when(roleRepository.findByName(Role.RoleName.ROLE_ADMIN)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.editRoles(request, 3L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Role by name: ROLE_ADMIN");

        verify(repository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    @DisplayName("Should return the full profile when the caller is an admin")
    void shouldGetFullUserForAdmin() {
        User user = user("user@test.com");
        UserResponse expected = userResponse();

        when(repository.findById(3L)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(expected);

        Object response = userService.getUser(3L, adminAuth);

        assertThat(response).isEqualTo(expected);
    }

    @Test
    @DisplayName("Should return the public profile when the caller is not an admin")
    void shouldGetPublicUserForRegularUser() {
        User user = user("admin@test.com");
        PublicUserResponse expected = new PublicUserResponse(1L, "admin@test.com", "Admin");

        when(repository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toPublicResponse(user)).thenReturn(expected);

        Object response = userService.getUser(1L, userAuth);

        assertThat(response).isEqualTo(expected);
    }

    @Test
    @DisplayName("Should throw when the requested user does not exist")
    void shouldThrowWhenUserDoesNotExist() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUser(99L, userAuth))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("User by id: 99");
    }

    @Test
    @DisplayName("Should update the current user")
    void shouldUpdateCurrentUser() {
        User user = user("user@test.com");
        UserUpdateRequest request = new UserUpdateRequest("Updated", "Name");
        UserResponse expected = new UserResponse("user@test.com", "Updated", "Name", List.of());

        when(repository.findByEmail("user@test.com")).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(expected);

        UserResponse response = userService.updateCurrentUser(userAuth, request);

        assertThat(response).isEqualTo(expected);
        verify(userMapper).updateUser(request, user);
    }

    @Test
    @DisplayName("Should throw when updating a missing current user")
    void shouldThrowWhenUpdatingMissingCurrentUser() {
        UserUpdateRequest request = new UserUpdateRequest("Updated", "Name");
        when(repository.findByEmail("user@test.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateCurrentUser(userAuth, request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("User by email: user@test.com");
    }

    @Test
    @DisplayName("Should update a user by id")
    void shouldUpdateUser() {
        User user = user("user@test.com");
        UserUpdateRequest request = new UserUpdateRequest("Updated", "Name");
        UserResponse expected = new UserResponse("user@test.com", "Updated", "Name", List.of());

        when(repository.findById(3L)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(expected);

        UserResponse response = userService.updateUser(request, 3L);

        assertThat(response).isEqualTo(expected);
        verify(userMapper).updateUser(request, user);
    }

    @Test
    @DisplayName("Should throw when updating a missing user by id")
    void shouldThrowWhenUpdatingMissingUser() {
        UserUpdateRequest request = new UserUpdateRequest("Updated", "Name");
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateUser(request, 99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("User by id: 99");
    }

    private Authentication authentication(String email, String role) {
        return new UsernamePasswordAuthenticationToken(
                email,
                null,
                List.of(new SimpleGrantedAuthority(role))
        );
    }

    private User user(String email) {
        User user = new User();
        user.setId(3L);
        user.setEmail(email);
        user.setFirstName("User");
        user.setLastName("User");
        return user;
    }

    private Role role(Role.RoleName name) {
        Role role = new Role();
        role.setName(name);
        return role;
    }

    private UserResponse userResponse() {
        return new UserResponse("user@test.com", "User", "User", List.of());
    }
}
