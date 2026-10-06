package com.userservice.controller;

import com.userservice.dto.user.PublicUserResponse;
import com.userservice.dto.user.UserResponse;
import com.userservice.dto.user.UpdateRolesRequest;
import com.userservice.dto.user.UserUpdateRequest;
import com.userservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User management", description = "Endpoints for user management")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    @Operation(summary = "Get information about current authenticated user")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'ORGANIZER')")
    public ResponseEntity<UserResponse> getCurrentUser(
            Authentication authentication
    ) {
        return ResponseEntity.ok(userService.getCurrentUser(authentication));
    }

    @PutMapping("/me")
    @Operation(summary = "Update current authenticated user's data")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'ORGANIZER')")
    public ResponseEntity<UserResponse> updateCurrentUser(
            Authentication authentication,
            @RequestBody @Valid UserUpdateRequest request
            ) {
        return ResponseEntity.ok(userService.updateCurrentUser(authentication, request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update user's data by id")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @RequestBody @Valid UserUpdateRequest request
    ) {
        return ResponseEntity.ok(userService.updateUser(request, id));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by id")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'ORGANIZER')")
    public ResponseEntity<?> getUser(
            @PathVariable Long id,
            Authentication authentication
    ) {
        return ResponseEntity.ok(userService.getUser(id, authentication));
    }

    @PutMapping("/roles/{id}")
    @Operation(summary = "Edit roles for user by id")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> editRoles(
            @RequestBody @Valid UpdateRolesRequest request,
            @PathVariable Long id
    ) {
        userService.editRoles(request, id);

        return ResponseEntity.noContent().build();
    }
}
