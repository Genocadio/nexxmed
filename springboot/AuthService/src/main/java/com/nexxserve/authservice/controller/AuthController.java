package com.nexxserve.authservice.controller;

import com.nexxserve.authservice.dto.*;
import com.nexxserve.authservice.service.AuthService;
import com.nexxserve.authservice.service.PermissionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final PermissionService permissionService;

    @Autowired
    public AuthController(AuthService authService, PermissionService permissionService) {
        this.authService = authService;
        this.permissionService = permissionService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        System.out.println("Registering user: " + request);
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/users/{userId}/roles")
    public ResponseEntity<UserAuthorizationsResponse> addRolesToUser(@PathVariable String userId, @RequestBody ServiceAuthorizationRequest request) {
        UserAuthorizationsResponse response = null;

        // Iterate through each service and its roles in the serviceAuthorizations map
        for (Map.Entry<String, ServiceAuthorizationRequest.ServiceRoles> entry : request.getServiceAuthorizations().entrySet()) {
            String serviceName = entry.getKey();
            Set<String> roles = entry.getValue().getRoles();

            // For each role in the current service, assign it to the user
            for (String role : roles) {
                response = authService.addRoleToUser(userId, serviceName, role);
            }
        }

        return ResponseEntity.ok(response);
    }

    // Merged endpoints from PermissionController
    @GetMapping("/permissions")
//    @PreAuthorize("@security.hasRole('USER')")
    public ResponseEntity<List<ServicePermissionResponse>> getAllServicePermissions() {
        return ResponseEntity.ok(permissionService.getAllServicePermissions());
    }

    @GetMapping("/permissions/{serviceName}")
    public ResponseEntity<ServicePermissionResponse> getServicePermissions(@PathVariable String serviceName) {
        return ResponseEntity.ok(permissionService.getServicePermissions(serviceName));
    }
}