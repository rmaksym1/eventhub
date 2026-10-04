package com.userservice.controller;

import com.userservice.dto.user.UserResponse;
import com.userservice.dto.user.UpdateRolesRequest;
import com.userservice.entity.user.User;
import com.userservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User management", description = "Endpoints for user management")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    @Operation(summary = "Get information about current authenticated user")
    @PreAuthorize("hasRole('USER')")
    ResponseEntity<UserResponse> getCurrentUser(
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(userService.getCurrentUser(user));
    }

    @PutMapping("/roles/{id}")
    @Operation(summary = "Edit roles for user by id")
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<Void> editRoles(
            @RequestBody UpdateRolesRequest request,
            @RequestParam Long id
    ) {
        userService.editRoles(request, id);

        return ResponseEntity.noContent().build();
    }
}
