package com.nexxserve.authservice.service;

import com.nexxserve.authservice.dto.*;
import com.nexxserve.authservice.exception.UserAlreadyExistsException;
import com.nexxserve.authservice.exception.UserServiceException;
import com.nexxserve.authservice.model.Services;
import com.nexxserve.authservice.model.ServiceRole;
import com.nexxserve.authservice.model.ServicePermission;
import com.nexxserve.authservice.model.UserCredential;
import com.nexxserve.authservice.repository.ServiceRepository;
import com.nexxserve.authservice.repository.ServiceRoleRepository;
import com.nexxserve.authservice.repository.UserCredentialRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AuthService {

    private final UserCredentialRepository userCredentialRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserGrpcService userGrpcService;
    private final ServiceRoleRepository serviceRoleRepository;
    private final ServiceRepository serviceRepository;

    @Value("${spring.application.name}")
    private String applicationName;

    @Autowired
    public AuthService(UserCredentialRepository userCredentialRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       AuthenticationManager authenticationManager,
                       UserGrpcService userGrpcService,
                       ServiceRoleRepository serviceRoleRepository,
                       ServiceRepository serviceRepository
    ) {
        this.userCredentialRepository = userCredentialRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.userGrpcService = userGrpcService;
        this.serviceRoleRepository = serviceRoleRepository;
        this.serviceRepository = serviceRepository;
    }

   @Transactional
   public AuthResponse register(RegisterRequest request) {
       try {
           // Create user in User Service first
           UserDTO userDTO = new UserDTO();
           userDTO.setFirstName(request.getFirstName());
           userDTO.setLastName(request.getLastName());
           userDTO.setUsername(request.getUsername());
           userDTO.setEmail(request.getEmail());
           userDTO.setPhone(request.getPhone());
           userDTO.setProfileUrl(request.getProfileUrl());
           userDTO.setClinicId(request.getClinicId());

           // Use the gRPC service to create the user
           UserDTO createdUser = userGrpcService.createUser(userDTO).join();
           if(createdUser.hasError()){
                throw new UserAlreadyExistsException("User creation failed: " + createdUser.getErrorMessage(), createdUser.getErrorCode());
           }

           // Initialize sets for service roles and permissions
           Set<ServiceRole> userServiceRoles = new HashSet<>();


           // Store credentials in Auth Service
           UserCredential userCredential = UserCredential.builder()
                   .userId(createdUser.getUserId())
                   .password(passwordEncoder.encode(request.getPassword()))
                   .serviceRoles(userServiceRoles)
                   .build();

           userCredentialRepository.save(userCredential);

           // Generate JWT with categorized roles and permissions
           assignRoleToUser(createdUser.getUserId(), applicationName, "USER");
           userCredential = userCredentialRepository.findByUserIdWithRolesAndPermissions(createdUser.getUserId())
                   .orElseThrow(() -> new UsernameNotFoundException("Error registering user: " + createdUser.getUserId()));
           Map<String, AuthResponse.ServiceAuthorizationDetails> serviceAuthorizations =
                   groupUserAuthorizationsByService(userCredential);

           // Generate updated auth response with JWT token
           return getAuthResponse(createdUser, userCredential, serviceAuthorizations);

       } catch (Exception e) {
           System.err.println("Critical error in register method: " + e.getMessage());
           log.error("Critical error in register method: {}", e.getMessage());
           throw e; // Re-throw to be caught by the controller
       }
   }

    @Transactional
    public UserAuthorizationsResponse addRoleToUser(String userId, String serviceName, String roleName) {
        try {
            // Assign role and get the result
            Boolean success = assignRoleToUser(userId, serviceName, roleName);
            if (!success) {
                throw new RuntimeException("Failed to assign role to user");
            }

            // Find user credentials with roles and permissions fetched
            UserCredential userCredential = userCredentialRepository.findByUserIdWithRolesAndPermissions(userId)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with ID: " + userId));

            // Group permissions by service name and create service authorizations
            Map<String, AuthResponse.ServiceAuthorizationDetails> authResponseDetails =
                    groupUserAuthorizationsByService(userCredential);

            // Convert AuthResponse.ServiceAuthorizationDetails to UserAuthorizationsResponse.AuthorizationDetails
            Map<String, UserAuthorizationsResponse.AuthorizationDetails> authDetails = new HashMap<>();

            authResponseDetails.forEach((service, details) -> {
                authDetails.put(service, new UserAuthorizationsResponse.AuthorizationDetails(
                        details.getRoles(),
                        details.getPermissions()
                ));
            });

            // Build and return the simplified response
            return UserAuthorizationsResponse.builder()
                    .userId(userId)
                    .serviceAuthorizations(authDetails)
                    .build();
        } catch (Exception e) {
            log.error("Error assigning role to user: {}", e.getMessage(), e);
            throw e;
        }
    }


    @Transactional
    public Boolean assignRoleToUser(String userId, String serviceName, String roleName) {
        try {
            // Check for null user
            if (userId == null ) {
                throw new IllegalArgumentException("User or userId is null");
            }

            // Find user credentials with roles and permissions fetched
            UserCredential userCredential = userCredentialRepository.findByUserIdWithRolesAndPermissions(userId)
                    .orElse(userCredentialRepository.findByUserId(userId)
                            .orElseThrow(() -> new UsernameNotFoundException("User not found with ID: " + userId)));

            // Find service - add logging to help debug
            log.debug("Looking for service with name: {}", serviceName);
            Services service = serviceRepository.findByName(serviceName)
                    .orElseThrow(() -> new IllegalArgumentException("Service not found: " + serviceName));

            // Find role under the service with permissions fetched - add logging to help debug
            log.debug("Looking for role {} under service {}", roleName, serviceName);
            ServiceRole role = serviceRoleRepository.findByNameAndServiceWithPermissions(roleName, service)
                    .orElse(serviceRoleRepository.findByNameAndService(roleName, service)
                            .orElseThrow(() -> new IllegalArgumentException(
                                    String.format("Role '%s' not found for service '%s'", roleName, serviceName))));

            // Initialize the collection if null
            if (userCredential.getServiceRoles() == null) {
                userCredential.setServiceRoles(new HashSet<>());
            }

            // Check if role is already assigned before adding
            boolean roleAlreadyExists = userCredential.getServiceRoles().stream()
                    .anyMatch(existingRole -> existingRole.getId().equals(role.getId()));

            if (!roleAlreadyExists) {
                // Create a new HashSet to avoid collection modification issues
                Set<ServiceRole> updatedRoles = new HashSet<>(userCredential.getServiceRoles());
                updatedRoles.add(role);
                userCredential.setServiceRoles(updatedRoles);

                userCredentialRepository.save(userCredential);

                // Force flush to ensure database consistency
                userCredentialRepository.flush();
            }

            // Refresh the entity to get the latest state from database
            userCredential = userCredentialRepository.findByUserId(userId)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found after update"));

            // Create service authorizations safely
            Map<String, AuthResponse.ServiceAuthorizationDetails> serviceAuthorizations =
                    groupUserAuthorizationsByService(userCredential);

            // Generate updated auth response with JWT token
            return true;
        } catch (Exception e) {
            // Improve error logging by including the stack trace
            log.error("Critical error in assignRoleToUser method: {}", e.getMessage(), e);
            throw e;
        }
    }
    private Map<String, AuthResponse.ServiceAuthorizationDetails> buildServiceAuthorizations(
            Map<String, Set<String>> rolesByService,
            Map<String, Set<String>> permissionsByService) {

        Map<String, AuthResponse.ServiceAuthorizationDetails> serviceAuthorizations = new HashMap<>();

        for (String serviceName : permissionsByService.keySet()) {
            serviceAuthorizations.put(serviceName,
                    AuthResponse.ServiceAuthorizationDetails.builder()
                            .roles(rolesByService.getOrDefault(serviceName, Collections.emptySet()))
                            .permissions(permissionsByService.getOrDefault(serviceName, Collections.emptySet()))
                            .build()
            );
        }

        return serviceAuthorizations;
    }

    public AuthResponse login(AuthRequest request) {
        try {
            // Get user from User Service by username
            UserDTO user = findUserByUsername(request.getUsername());

            // Authenticate with user ID and password
            Authentication authentication;
            try {
                authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUserId(), request.getPassword())
                );
            } catch (org.springframework.security.authentication.BadCredentialsException e) {
                throw new org.springframework.security.authentication.AuthenticationCredentialsNotFoundException(
                    "Invalid username or password", e);
            }

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Get user credentials from Auth Service
            UserCredential userCredential = userCredentialRepository.findByUserId(user.getUserId())
                    .orElseThrow(() -> new org.springframework.security.authentication.AuthenticationCredentialsNotFoundException(
                        "User credentials not found"));

            // Group permissions by service name and create service authorizations
            Map<String, AuthResponse.ServiceAuthorizationDetails> serviceAuthorizations =
                    groupUserAuthorizationsByService(userCredential);

            // Generate JWT with user details and service authorizations
            return getAuthResponse(user, userCredential, serviceAuthorizations);
        } catch (UsernameNotFoundException | org.springframework.security.authentication.AuthenticationCredentialsNotFoundException e) {
            // Wrap these authentication-related exceptions to be translated to 401 in the controller
            throw new org.springframework.security.authentication.AuthenticationCredentialsNotFoundException(
                "Invalid username or password", e);
        }
    }

    private AuthResponse getAuthResponse(UserDTO user, UserCredential userCredential, Map<String, AuthResponse.ServiceAuthorizationDetails> serviceAuthorizations) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("serviceAuthorizations", serviceAuthorizations);

        String jwt = jwtService.generateToken(loadUserDetails(userCredential), extraClaims);

        return AuthResponse.builder()
                .token(jwt)
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .userId(user.getUserId())
                .serviceAuthorizations(serviceAuthorizations)
                .build();
    }

    private Map<String, AuthResponse.ServiceAuthorizationDetails> groupUserAuthorizationsByService(UserCredential userCredential) {
        Map<String, AuthResponse.ServiceAuthorizationDetails> serviceAuthorizations = new HashMap<>();

        // Defensive copy to avoid concurrent modification
        Set<ServiceRole> userRoles = new HashSet<>(userCredential.getServiceRoles());

        // Group roles by service and collect their permissions
        Map<Services, Set<ServiceRole>> rolesByService = new HashMap<>();

        // Organize roles by their service
        for (ServiceRole role : userRoles) {
            // Ensure service is loaded
            Services service = role.getService();
            if (service != null) {
                rolesByService.computeIfAbsent(service, k -> new HashSet<>()).add(role);
            }
        }

        // For each service, build the authorization details
        for (Map.Entry<Services, Set<ServiceRole>> entry : rolesByService.entrySet()) {
            Services service = entry.getKey();
            Set<ServiceRole> roles = entry.getValue();

            // Extract role names
            Set<String> roleNames = roles.stream()
                    .map(ServiceRole::getName)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            // Collect all permissions from these roles
            Set<String> permissionNames = new HashSet<>();
            for (ServiceRole role : roles) {
                // Safely iterate through permissions
                Set<ServicePermission> permissions = role.getPermissions();
                if (permissions != null) {
                    // Create defensive copy to avoid concurrent modification
                    Set<ServicePermission> permissionsCopy = new HashSet<>(permissions);
                    for (ServicePermission permission : permissionsCopy) {
                        if (permission != null && permission.getPermission() != null) {
                            permissionNames.add(permission.getPermission());
                        }
                    }
                }
            }

            // Add to service authorizations map
            if (service.getName() != null) {
                serviceAuthorizations.put(service.getName(),
                        AuthResponse.ServiceAuthorizationDetails.builder()
                                .roles(roleNames)
                                .permissions(permissionNames)
                                .build()
                );
            }
        }

        return serviceAuthorizations;
    }

    // Helper method to find user by username
    private UserDTO findUserByUsername(String username) {
        try {
            // Use the gRPC search method to find user by username, email, or phone
            return userGrpcService.searchUser(username).join();
        } catch (Exception e) {
            if (e.getCause() instanceof UserServiceException use) {
                if (use.getStatusCode() == HttpStatus.NOT_FOUND.value()) {
                    throw new UsernameNotFoundException("User not found with identifier: " + username);
                }
                throw use;
            }
            throw e;
        }
    }

    // Helper method to convert UserCredential to UserDetails
    private org.springframework.security.core.userdetails.UserDetails loadUserDetails(UserCredential userCredential) {
        List<org.springframework.security.core.authority.SimpleGrantedAuthority> authorities = new ArrayList<>();

        // Add roles with ROLE_ prefix
        for (ServiceRole role : userCredential.getServiceRoles()) {
            authorities.add(new org.springframework.security.core.authority.SimpleGrantedAuthority(
                    "ROLE_" + role.getName()));

            // Add permissions from this role
            for (ServicePermission permission : role.getPermissions()) {
                authorities.add(new org.springframework.security.core.authority.SimpleGrantedAuthority(
                        permission.getPermission()));
            }
        }

        return new org.springframework.security.core.userdetails.User(
                userCredential.getUserId(),
                userCredential.getPassword(),
                authorities
        );
    }
}