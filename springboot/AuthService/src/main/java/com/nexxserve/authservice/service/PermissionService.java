package com.nexxserve.authservice.service;

import com.nexxserve.authservice.dto.PermissionDTO;
import com.nexxserve.authservice.dto.ServicePermissionResponse;
import com.nexxserve.authservice.model.Services;
import com.nexxserve.authservice.model.ServicePermission;
import com.nexxserve.authservice.model.ServiceRole;
import com.nexxserve.authservice.repository.ServicePermissionRepository;
import com.nexxserve.authservice.repository.ServiceRepository;
import com.nexxserve.authservice.repository.ServiceRoleRepository;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.event.EventListener;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.nexxserve.billing.grpc.PermissionsServiceGrpc;
import com.nexxserve.billing.grpc.PermissionsRequest;
import com.nexxserve.billing.grpc.PermissionsResponse;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PermissionService {

    private final ServicePermissionRepository servicePermissionRepository;
    private final ServiceRepository serviceRepository;
    private final ServiceRoleRepository serviceRoleRepository;
    private final RestTemplate restTemplate;
    private final DiscoveryClient discoveryClient;

    // Create a temporary map to store permission data before processing
    private final Map<String, Map<String, List<PermissionDTO>>> tempPermissionsStorage = new HashMap<>();

    @Value("${services.list:billing,user,appointment}")
    private List<String> servicesList;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void fetchAllServicePermissionsOnStartup() {
        log.info("Starting to fetch permissions from all registered services");

        // Clear the temporary storage before starting
        tempPermissionsStorage.clear();

        for (String serviceName : servicesList) {
            try {
                fetchAndSaveServicePermissionsGrpc(serviceName);
                // fetchAndSaveServicePermissions(serviceName);
            } catch (Exception e) {
                log.error("Failed to fetch permissions for service: {}", serviceName, e);
            }
        }
        updateServiceRolesWithPermissions();
    }


    @Transactional
    public void fetchAndSaveServicePermissionsGrpc(String serviceName) {
        log.info("Fetching permissions for service {} via gRPC", serviceName);

        try {
            List<ServiceInstance> instances = discoveryClient.getInstances(serviceName);

            if (instances.isEmpty()) {
                log.error("No instances found for service: {}", serviceName);
                throw new IllegalStateException("Service " + serviceName + " not found in service registry");
            }

            ServiceInstance serviceInstance = instances.getFirst();
            String host = serviceInstance.getHost();
            int port = Integer.parseInt(serviceInstance.getMetadata().get("grpc-port"));
            log.info("Connecting to gRPC service {} at {}:{}", serviceName, host, port);

            ManagedChannel channel = ManagedChannelBuilder
                    .forAddress(host, port)
                    .usePlaintext()
                    .build();

            PermissionsServiceGrpc.PermissionsServiceBlockingStub stub =
                    PermissionsServiceGrpc.newBlockingStub(channel);

            PermissionsRequest request = PermissionsRequest.newBuilder().build();

            PermissionsResponse grpcResponse = stub.getPermissions(request);

            try {
                // Create or get service entity
                Services serviceEntity = serviceRepository.findByName(serviceName)
                        .orElseGet(() -> {
                            Services newService = Services.builder()
                                    .name(serviceName)
                                    .build();
                            return serviceRepository.save(newService);
                        });

                // Process permissions
                List<PermissionDTO> permissions = grpcResponse.getPermissionsList().stream()
                        .map(p -> new PermissionDTO(
                                p.getPermission(),
                                p.getDescription(),
                                p.getRole()))
                        .collect(Collectors.toList());

                // Store in temporary storage instead of directly in DB
                storePermissionsInTempStorage(serviceName, permissions);

                log.info("Fetched {} permissions via gRPC for service: {}", permissions.size(), serviceName);
            } finally {
                channel.shutdown();
            }
        } catch (Exception e) {
            log.error("Error calling gRPC service {}: {}", serviceName, e.getMessage());
            throw e;
        }
    }

    @Transactional
    public void fetchAndSaveServicePermissions(String serviceName) {
        log.info("Fetching permissions for service: {}", serviceName);

        List<ServiceInstance> instances = discoveryClient.getInstances(serviceName);

        if (instances.isEmpty()) {
            log.error("No instances found for service: {}", serviceName);
            throw new IllegalStateException("Service " + serviceName + " not found in service registry");
        }

        ServiceInstance serviceInstance = instances.getFirst();
        String url = serviceInstance.getUri() + "/" + serviceName + "/permissions";

        try {
            ResponseEntity<ServicePermissionResponse> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<ServicePermissionResponse>() {}
            );

            if (response.getBody() != null) {
                ServicePermissionResponse permissionResponse = response.getBody();
                log.info("Fetched permissions for service: {} - {}", serviceName, permissionResponse);

                // Create or get service entity
                Services serviceEntity = serviceRepository.findByName(serviceName)
                        .orElseGet(() -> {
                            Services newService = Services.builder()
                                    .name(serviceName)
                                    .build();
                            return serviceRepository.save(newService);
                        });

                List<PermissionDTO> permissions = permissionResponse.getPermissions();

                // Store in temporary storage instead of directly in DB
                storePermissionsInTempStorage(serviceName, permissions);
            }
        } catch (RestClientException e) {
            log.error("Error calling service {} at URL {}: {}", serviceName, url, e.getMessage());
            throw e;
        }
    }

    private void storePermissionsInTempStorage(String serviceName, List<PermissionDTO> permissions) {
        // Group permissions by role
        Map<String, List<PermissionDTO>> permissionsByRole = permissions.stream()
                .collect(Collectors.groupingBy(PermissionDTO::getRole));

        // Store in the temporary map
        tempPermissionsStorage.put(serviceName, permissionsByRole);
    }

    @Transactional
    public void updateServiceRolesWithPermissions() {
        log.info("Updating service roles with permissions");

        // Process the temporary storage map
        for (Map.Entry<String, Map<String, List<PermissionDTO>>> serviceEntry : tempPermissionsStorage.entrySet()) {
            String serviceName = serviceEntry.getKey();

            // Find or create the service
            Services serviceEntity = serviceRepository.findByName(serviceName)
                    .orElseGet(() -> {
                        Services newService = Services.builder()
                                .name(serviceName)
                                .build();
                        return serviceRepository.save(newService);
                    });

            // For each role in the service
            Map<String, List<PermissionDTO>> rolePermissions = serviceEntry.getValue();
            for (Map.Entry<String, List<PermissionDTO>> roleEntry : rolePermissions.entrySet()) {
                String roleName = roleEntry.getKey();
                List<PermissionDTO> permissions = roleEntry.getValue();

                // Find or create the service role
                ServiceRole serviceRole = serviceRoleRepository.findByNameAndService(roleName, serviceEntity)
                        .orElseGet(() -> {
                            ServiceRole newRole = ServiceRole.builder()
                                    .name(roleName)
                                    .service(serviceEntity)
                                    .build();
                            return serviceRoleRepository.save(newRole);
                        });

                // Create new permission entities attached to the service role
                for (PermissionDTO permissionDTO : permissions) {
                    // Check if the permission already exists for this role
                    boolean exists = serviceRole.getPermissions().stream()
                            .anyMatch(p -> p.getPermission().equals(permissionDTO.getPermission()));

                    if (!exists) {
                        ServicePermission newPermission = ServicePermission.builder()
                                .permission(permissionDTO.getPermission())
                                .description(permissionDTO.getDescription())
                                .role(serviceRole)
                                .build();

                        servicePermissionRepository.save(newPermission);
                    }
                }
            }
        }

        log.info("Service roles and permissions hierarchy updated successfully");

        // Clear the temporary storage after processing
        tempPermissionsStorage.clear();
    }

    public ServicePermissionResponse getServicePermissions(String serviceName) {
        // Find the service
        Services serviceEntity = serviceRepository.findByName(serviceName)
                .orElseThrow(() -> new IllegalStateException("Service " + serviceName + " not found"));

        // Get all roles for the service
        List<ServiceRole> roles = serviceRoleRepository.findByService(serviceEntity);

        List<PermissionDTO> permissionDTOs = new ArrayList<>();

        // Collect all permissions from all roles in this service
        for (ServiceRole role : roles) {
            for (ServicePermission permission : role.getPermissions()) {
                permissionDTOs.add(new PermissionDTO(
                    permission.getPermission(),
                    permission.getDescription(),
                    role.getName()  // Use the role name as the "defaultRole"
                ));
            }
        }

        return new ServicePermissionResponse(permissionDTOs, serviceName);
    }

    public List<ServicePermissionResponse> getAllServicePermissions() {
        List<Services> allServices = serviceRepository.findAll();
        List<ServicePermissionResponse> result = new ArrayList<>();

        for (Services service : allServices) {
            List<ServiceRole> roles = serviceRoleRepository.findByService(service);
            List<PermissionDTO> permissionDTOs = new ArrayList<>();

            for (ServiceRole role : roles) {
                for (ServicePermission permission : role.getPermissions()) {
                    permissionDTOs.add(new PermissionDTO(
                        permission.getPermission(),
                        permission.getDescription(),
                        role.getName()
                    ));
                }
            }

            result.add(new ServicePermissionResponse(permissionDTOs, service.getName()));
        }

        return result;
    }
}