package com.nexxserve.authservice.controller;

import com.nexxserve.authservice.dto.ServicePermissionResponse;
import com.nexxserve.authservice.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    @GetMapping
//    @PreAuthorize("@security.hasRole('USER')")
    public ResponseEntity<List<ServicePermissionResponse>> getAllServicePermissions() {
        return ResponseEntity.ok(permissionService.getAllServicePermissions());
    }

    @GetMapping("/{serviceName}")
    public ResponseEntity<ServicePermissionResponse> getServicePermissions(@PathVariable String serviceName) {
        return ResponseEntity.ok(permissionService.getServicePermissions(serviceName));
    }
}