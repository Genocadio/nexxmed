package com.nexxserve.inventoryservice.controller;

import com.nexxserve.inventoryservice.dto.user.*;
import com.nexxserve.inventoryservice.security.RequireClientActivation;
import com.nexxserve.inventoryservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequireClientActivation(message = "Client is not activated. Please contact support.")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    private final UserService userService;

    @PutMapping("/users/{id}/roles")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> updateUserRoles(
            @PathVariable Long id,
            @Valid @RequestBody UserRoleUpdateDto roleUpdateDto) {
        UserDto updatedUser = userService.updateUserRoles(id, roleUpdateDto);
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping("/users")
//    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<Page<UserDto>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String sort) {
        Page<UserDto> users = userService.getAllUsers(page, size, sort);
        return ResponseEntity.ok(users);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody LoginRequestDto request) {
        AuthResponseDto response = userService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(@Valid @RequestBody RegisterRequestDto request) {
        AuthResponseDto response = userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDto> refreshToken(@RequestBody RefreshTokenRequestDto request) {
        AuthResponseDto response = userService.refreshToken(request.getRefreshToken());
        return ResponseEntity.ok(response);
    }



    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDto> getCurrentUser() {
        UserDto user = userService.getCurrentUser();
        return ResponseEntity.ok(user);
    }

    @PutMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDto> updateCurrentUser(@Valid @RequestBody UserUpdateDto updateDto) {
        Long userId = userService.getCurrentUserId();
        UserDto updatedUser = userService.updateUser(userId, updateDto);
        return ResponseEntity.ok(updatedUser);
    }

    @PostMapping("/change-password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody PasswordChangeDto passwordChangeDto) {
        Long userId = userService.getCurrentUserId();
        userService.changePassword(userId, passwordChangeDto);
        return ResponseEntity.ok().build();
    }

    // Admin endpoints
    @GetMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        UserDto user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/users/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> updateUserStatus(
            @PathVariable Long id,
            @Valid @RequestBody UserStatusUpdateDto statusUpdateDto) {
        UserDto updatedUser = userService.updateUserStatus(id, statusUpdateDto);
        return ResponseEntity.ok(updatedUser);
    }

    @PutMapping("/users/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deactivateUser(@PathVariable Long id) {
        userService.deactivateUser(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/users/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> activateUser(@PathVariable Long id) {
        userService.activateUser(id);
        return ResponseEntity.ok().build();
    }
}

