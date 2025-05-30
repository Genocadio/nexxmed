package com.nexxserve.authservice.service;

import com.nexxserve.authservice.model.ServicePermission;
import com.nexxserve.authservice.model.ServiceRole;
import com.nexxserve.authservice.model.Services;
import com.nexxserve.authservice.repository.ServicePermissionRepository;
import com.nexxserve.authservice.repository.ServiceRepository;
import com.nexxserve.authservice.repository.ServiceRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AuthServicePermissionInitializer implements ApplicationRunner {

    private final Environment environment;
    private final ServiceRepository serviceRepository;
    private final ServiceRoleRepository serviceRoleRepository;
    private final ServicePermissionRepository servicePermissionRepository;

    @Autowired
    public AuthServicePermissionInitializer(Environment environment,
                                          ServiceRepository serviceRepository,
                                          ServiceRoleRepository serviceRoleRepository,
                                          ServicePermissionRepository servicePermissionRepository) {
        this.environment = environment;
        this.serviceRepository = serviceRepository;
        this.serviceRoleRepository = serviceRoleRepository;
        this.servicePermissionRepository = servicePermissionRepository;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        String serviceName = environment.getProperty("spring.application.name", "auth-service");
        initializeAuthServicePermissions(serviceName);
    }

    private void initializeAuthServicePermissions(String serviceName) {
        // Define permissions for different roles
        Map<String, List<String>> rolePermissions = new HashMap<>();

        // Admin permissions
        rolePermissions.put("ADMIN", Arrays.asList(
            "users.create", "users.read", "users.update", "users.delete",
            "roles.create", "roles.read", "roles.update", "roles.delete",
            "permissions.create", "permissions.read", "permissions.update", "permissions.delete"
        ));

        // Manager permissions
        rolePermissions.put("MANAGER", Arrays.asList(
            "users.read", "users.update",
            "roles.read",
            "permissions.read"
        ));

        // User permissions
        rolePermissions.put("USER", Arrays.asList(
            "users.read-self",
            "roles.read-self",
            "permissions.read-self"
        ));

        // Permission descriptions
        Map<String, String> permissionDescriptions = new HashMap<>();
        permissionDescriptions.put("users.create", "Create user accounts");
        permissionDescriptions.put("users.read", "View all user accounts");
        permissionDescriptions.put("users.read-self", "View own user account");
        permissionDescriptions.put("users.update", "Update user accounts");
        permissionDescriptions.put("users.delete", "Delete user accounts");

        permissionDescriptions.put("roles.create", "Create roles");
        permissionDescriptions.put("roles.read", "View all roles");
        permissionDescriptions.put("roles.read-self", "View own roles");
        permissionDescriptions.put("roles.update", "Update roles");
        permissionDescriptions.put("roles.delete", "Delete roles");

        permissionDescriptions.put("permissions.create", "Create permissions");
        permissionDescriptions.put("permissions.read", "View all permissions");
        permissionDescriptions.put("permissions.read-self", "View own permissions");
        permissionDescriptions.put("permissions.update", "Update permissions");
        permissionDescriptions.put("permissions.delete", "Delete permissions");

        // Create or find service
        Services service = serviceRepository.findByName(serviceName)
                .orElseGet(() -> {
                    Services newService = Services.builder()
                            .name(serviceName)
                            .description("Authentication and Authorization Service")
                            .build();
                    return serviceRepository.save(newService);
                });

        log.info("Initializing permissions for service: {}", serviceName);

        // Create roles with permissions
        for (Map.Entry<String, List<String>> entry : rolePermissions.entrySet()) {
            String roleName = entry.getKey();
            List<String> permissions = entry.getValue();

            // Find or create the role
            // Find or create the role
            ServiceRole role = serviceRoleRepository.findByNameAndService(roleName, service)
                    .orElseGet(() -> {
                        ServiceRole newRole = ServiceRole.builder()
                                .name(roleName)
                                .service(service)
                                .permissions(new HashSet<>()) // Initialize permissions set
                                .build();
                        return serviceRoleRepository.save(newRole);
                    });

            // Create and add permissions to the role
            for (String permissionName : permissions) {
                String fullPermissionName = serviceName + "." + permissionName;

                // Check if permission already exists for this role
                // At line 124, rename the local variable to avoid the naming conflict
                Set<ServicePermission> existingPermissions = role.getPermissions();
                boolean permissionExists = existingPermissions != null &&
                        existingPermissions.stream()
                            .anyMatch(p -> p.getPermission().equals(fullPermissionName));

                if (!permissionExists) {
                    ServicePermission permission = ServicePermission.builder()
                            .permission(fullPermissionName)
                            .description(permissionDescriptions.getOrDefault(permissionName, ""))
                            .role(role)
                            .build();

                    servicePermissionRepository.save(permission);

                    // Initialize the permissions collection if it's null
                    if (role.getPermissions() == null) {
                        role.setPermissions(new HashSet<>());
                    }
                    role.getPermissions().add(permission);

                    log.debug("Created permission: {}", fullPermissionName);
                }
            }

            log.info("Initialized {} permissions for role: {}", permissions.size(), roleName);
        }

        log.info("Completed permission initialization for service: {}", serviceName);
    }
}