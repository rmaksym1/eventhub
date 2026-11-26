package com.userservice.service;

import com.eventhub.common.exception.EntityNotFoundException;
import com.eventhub.common.security.JwtUtil;
import com.userservice.dto.auth.AuthResponse;
import com.userservice.dto.auth.CreateUserRequest;
import com.userservice.dto.user.UserRequest;
import com.userservice.entity.user.RefreshToken;
import com.userservice.entity.user.Role;
import com.userservice.entity.user.User;
import com.userservice.exception.AuthenticationException;
import com.userservice.mapper.UserMapper;
import com.userservice.repository.RefreshTokenRepository;
import com.userservice.repository.RoleRepository;
import com.userservice.repository.UserRepository;
import com.userservice.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private JwtUtil jwtTokenUtil;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    @DisplayName("Should login and return access and refresh tokens")
    void shouldLogin() {
        UserRequest request = new UserRequest("user@test.com", "secret");
        User user = user(5L, "user@test.com", Role.RoleName.ROLE_USER);

        when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(user));
        when(jwtTokenUtil.generateToken(eq("user@test.com"), eq(5L), eq(List.of("ROLE_USER"))))
                .thenReturn("access-token");
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AuthResponse response = authService.login(request);

        assertThat(response.activeToken()).isEqualTo("access-token");
        assertThat(response.refreshToken()).isNotBlank();
        verify(authenticationManager).authenticate(any());

        ArgumentCaptor<RefreshToken> captor = ArgumentCaptor.forClass(RefreshToken.class);
        verify(refreshTokenRepository).save(captor.capture());
        assertThat(captor.getValue().getUsername()).isEqualTo("user@test.com");
        assertThat(captor.getValue().getToken()).isEqualTo(response.refreshToken());
        assertThat(captor.getValue().getExpiryDate()).isAfter(Instant.now());
    }

    @Test
    @DisplayName("Should throw when login email does not exist")
    void shouldThrowWhenLoginUserDoesNotExist() {
        UserRequest request = new UserRequest("missing@test.com", "secret");
        when(userRepository.findByEmail(request.email())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("User by email: missing@test.com");

        verifyNoInteractions(authenticationManager);
    }

    @Test
    @DisplayName("Should propagate bad credentials from the authentication manager")
    void shouldPropagateBadCredentials() {
        UserRequest request = new UserRequest("user@test.com", "wrong");
        User user = user(5L, "user@test.com", Role.RoleName.ROLE_USER);

        when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(user));
        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Bad credentials"));

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BadCredentialsException.class);

        verify(refreshTokenRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should register a user with the default role and return tokens")
    void shouldRegister() {
        CreateUserRequest request = new CreateUserRequest(
                "new@test.com", "New", "User", "secret", "secret"
        );
        User user = user(10L, "new@test.com", null);
        Role userRole = new Role();
        userRole.setName(Role.RoleName.ROLE_USER);

        when(userRepository.findByEmail(request.email())).thenReturn(Optional.empty());
        when(roleRepository.findByName(Role.RoleName.ROLE_USER)).thenReturn(Optional.of(userRole));
        when(userMapper.toEntity(request)).thenReturn(user);
        when(passwordEncoder.encode(request.password())).thenReturn("hashed");
        when(jwtTokenUtil.generateToken(eq("new@test.com"), eq(10L), eq(List.of("ROLE_USER"))))
                .thenReturn("access-token");
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AuthResponse response = authService.register(request);

        assertThat(response.activeToken()).isEqualTo("access-token");
        assertThat(response.refreshToken()).isNotBlank();
        assertThat(user.getPassword()).isEqualTo("hashed");
        assertThat(user.getRoles()).containsExactly(userRole);
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Should throw when the email is already taken")
    void shouldThrowWhenEmailIsTaken() {
        CreateUserRequest request = new CreateUserRequest(
                "user@test.com", "New", "User", "secret", "secret"
        );
        when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(user(1L, "user@test.com", Role.RoleName.ROLE_USER)));

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(AuthenticationException.class)
                .hasMessage("Email is taken!");

        verify(userRepository, never()).save(any());
        verifyNoInteractions(roleRepository);
    }

    @Test
    @DisplayName("Should throw when the default user role is missing")
    void shouldThrowWhenDefaultRoleIsMissing() {
        CreateUserRequest request = new CreateUserRequest(
                "new@test.com", "New", "User", "secret", "secret"
        );
        when(userRepository.findByEmail(request.email())).thenReturn(Optional.empty());
        when(roleRepository.findByName(Role.RoleName.ROLE_USER)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Role by name: ROLE_USER");

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should issue a new access token for a valid refresh token")
    void shouldRefreshToken() {
        RefreshToken refreshToken = refreshToken("refresh-token", "user@test.com", Instant.now().plus(1, ChronoUnit.DAYS));
        User user = user(5L, "user@test.com", Role.RoleName.ROLE_USER);

        when(refreshTokenRepository.findByToken("refresh-token")).thenReturn(Optional.of(refreshToken));
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));
        when(jwtTokenUtil.generateToken(eq("user@test.com"), eq(5L), anyList())).thenReturn("new-access");

        String accessToken = authService.refreshToken("refresh-token");

        assertThat(accessToken).isEqualTo("new-access");
    }

    @Test
    @DisplayName("Should throw when the refresh token does not exist")
    void shouldThrowWhenRefreshTokenDoesNotExist() {
        when(refreshTokenRepository.findByToken("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.refreshToken("missing"))
                .isInstanceOf(AuthenticationException.class)
                .hasMessageContaining("Refresh token by token: missing");

        verifyNoInteractions(userRepository);
    }

    @Test
    @DisplayName("Should throw when the refresh token is expired")
    void shouldThrowWhenRefreshTokenIsExpired() {
        RefreshToken refreshToken = refreshToken("expired", "user@test.com", Instant.now().minus(1, ChronoUnit.DAYS));
        when(refreshTokenRepository.findByToken("expired")).thenReturn(Optional.of(refreshToken));

        assertThatThrownBy(() -> authService.refreshToken("expired"))
                .isInstanceOf(AuthenticationException.class)
                .hasMessage("Refresh token is expired!");

        verifyNoInteractions(userRepository);
    }

    @Test
    @DisplayName("Should throw when the refresh token user does not exist")
    void shouldThrowWhenRefreshTokenUserDoesNotExist() {
        RefreshToken refreshToken = refreshToken("refresh-token", "missing@test.com", Instant.now().plus(1, ChronoUnit.DAYS));
        when(refreshTokenRepository.findByToken("refresh-token")).thenReturn(Optional.of(refreshToken));
        when(userRepository.findByEmail("missing@test.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.refreshToken("refresh-token"))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("User by email: missing@test.com");
    }

    private User user(Long id, String email, Role.RoleName roleName) {
        User user = new User();
        user.setId(id);
        user.setEmail(email);
        if (roleName != null) {
            Role role = new Role();
            role.setName(roleName);
            user.setRoles(Set.of(role));
        }
        return user;
    }

    private RefreshToken refreshToken(String token, String username, Instant expiryDate) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(token);
        refreshToken.setUsername(username);
        refreshToken.setExpiryDate(expiryDate);
        return refreshToken;
    }
}
