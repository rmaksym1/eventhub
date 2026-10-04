package com.userservice.service.impl;

import com.eventhub.common.exception.EntityNotFoundException;
import com.userservice.dto.user.AuthResponse;
import com.userservice.dto.user.CreateUserRequest;
import com.userservice.dto.user.UserRequest;
import com.userservice.entity.user.RefreshToken;
import com.userservice.entity.user.Role;
import com.userservice.entity.user.User;
import com.userservice.exception.AuthenticationException;
import com.userservice.mapper.UserMapper;
import com.userservice.repository.RefreshTokenRepository;
import com.userservice.repository.RoleRepository;
import com.userservice.repository.UserRepository;
import com.userservice.security.JwtUtil;
import com.userservice.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Override
    public AuthResponse login(UserRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        String accessToken = jwtUtil.generateToken(request.email());

        RefreshToken refreshToken = createRefreshToken(request.email());
        return new AuthResponse(accessToken, refreshToken.getToken());
    }

    @Override
    public AuthResponse register(CreateUserRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new AuthenticationException("Email is taken!");
        }

        Role userRole = roleRepository.findByName(Role.RoleName.ROLE_USER).orElseThrow(
                () -> new EntityNotFoundException("Role by name: " + Role.RoleName.ROLE_USER + " not found!")
        );

        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRoles(Set.of(userRole));

        userRepository.save(user);

        String accessToken = jwtUtil.generateToken(request.email());
        RefreshToken refreshToken = createRefreshToken(request.email());

        return new AuthResponse(accessToken, refreshToken.getToken());
    }

    @Override
    public String refreshToken(String token) {
        RefreshToken savedToken = refreshTokenRepository.findByToken(token).orElseThrow(
                () -> new AuthenticationException("Refresh token by token: " + token + " not found!")
        );

        if (savedToken.getExpiryDate().isBefore(Instant.now())) {
            throw new AuthenticationException("Refresh token is expired!");
        }

        return jwtUtil.generateToken(savedToken.getUsername());
    }

    public RefreshToken createRefreshToken(String username) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUsername(username);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(Instant.now().plus(Duration.ofDays(7)));

        return refreshTokenRepository.save(refreshToken);
    }
}
