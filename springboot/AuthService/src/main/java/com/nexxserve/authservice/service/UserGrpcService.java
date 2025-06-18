package com.nexxserve.authservice.service;

import com.nexxserve.authservice.dto.UserDTO;
import com.nexxserve.authservice.exception.BadArgumentException;
import com.nexxserve.authservice.exception.ResourceNotFoundException;
import com.nexxserve.authservice.exception.UserAlreadyExistsException;
import com.nexxserve.authservice.exception.UserServiceException;
import com.nexxserve.authservice.grpc.*;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.management.relation.RelationServiceNotRegisteredException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserGrpcService {

    private final DiscoveryClient discoveryClient;
    private static final String USER_SERVICE = "userService";

    // Constants for channel management
    private static final int SHUTDOWN_TIMEOUT_SECONDS = 5;

    @CircuitBreaker(name = USER_SERVICE, fallbackMethod = "searchUserFallback")
    @Retry(name = USER_SERVICE)
    @TimeLimiter(name = USER_SERVICE)
    public CompletableFuture<UserDTO> searchUser(String searchParameter) {
        return CompletableFuture.supplyAsync(() -> {
            ManagedChannel channel = null;
            try {
                String serviceName = "users";
                List<ServiceInstance> instances = discoveryClient.getInstances(serviceName);

                if (instances.isEmpty()) {
                    log.error("No instances found for service: {}", serviceName);
                    throw new IllegalStateException("Service " + serviceName + " not found in service registry");
                }

                ServiceInstance serviceInstance = instances.getFirst();
                String host = serviceInstance.getHost();
                int port = Integer.parseInt(serviceInstance.getMetadata().get("grpc-port"));
                log.info("Connecting to gRPC service {} at {}:{}", serviceName, host, port);

                // Create channel
                channel = ManagedChannelBuilder
                        .forAddress(host, port)
                        .usePlaintext()
                        .build();

                // Create stub using the channel
                UserServiceGrpc.UserServiceBlockingStub userServiceStub = UserServiceGrpc.newBlockingStub(channel);

                log.info("Sending gRPC searchUser request with parameter: {}", searchParameter);

                // Create search request
                SearchUserRequest request = SearchUserRequest.newBuilder()
                        .setSearchParameter(searchParameter)
                        .build();

                // Call the gRPC service
                SearchUserResponse response = userServiceStub.searchUser(request);

                // Map the response to UserDTO
                return UserDTO.builder()
                        .userId(response.getUserId())
                        .firstName(response.getFirstName())
                        .lastName(response.getLastName())
                        .email(response.getEmail())
                        .username(response.getUsername())
                        .phone(response.getPhone())
                        .profileUrl(response.getProfileUrl())
                        .clinicId(response.getClinicId())
                        .build();

            } catch (StatusRuntimeException e) {
                log.error("gRPC error while searching user with parameter: {}", searchParameter, e);
                // Map gRPC status codes to appropriate UserServiceException
                switch (e.getStatus().getCode()) {
                    case NOT_FOUND:
                        throw new UserServiceException("User not found: " + e.getStatus().getDescription(),
                                HttpStatus.NOT_FOUND.value());
                    case INVALID_ARGUMENT:
                        throw new UserServiceException("Invalid search parameter: " + e.getStatus().getDescription(),
                                HttpStatus.BAD_REQUEST.value());
                    default:
                        throw new UserServiceException("User service error: " + e.getStatus().getDescription(),
                                HttpStatus.INTERNAL_SERVER_ERROR.value());
                }
            } catch (Exception e) {
                log.error("Unexpected error in gRPC communication", e);
                throw new UserServiceException("Failed to search user via gRPC: " + e.getMessage(),
                        HttpStatus.INTERNAL_SERVER_ERROR.value());
            } finally {
                // Properly shutdown the channel
                shutdownChannel(channel);
            }
        });
    }

    // Fallback method for searchUser
    public CompletableFuture<UserDTO> searchUserFallback(String searchParameter, Exception e) {
        log.warn("Fallback: Unable to search user {}, using fallback", searchParameter, e);
        CompletableFuture<UserDTO> future = new CompletableFuture<>();
        future.completeExceptionally(new UserServiceException("User service temporarily unavailable",
                HttpStatus.SERVICE_UNAVAILABLE.value()));
        return future;
    }

    @CircuitBreaker(name = USER_SERVICE, fallbackMethod = "createUserFallback")
    @Retry(name = USER_SERVICE)
    @TimeLimiter(name = USER_SERVICE)
    public CompletableFuture<UserDTO> createUser(UserDTO userDTO) {
        return CompletableFuture.supplyAsync(() -> {
            ManagedChannel channel = null;
            try {
                String serviceName = "users";
                List<ServiceInstance> instances = discoveryClient.getInstances(serviceName);

                if (instances.isEmpty()) {
                    log.error("No instances found for service: {}", serviceName);
                    throw new IllegalStateException("Service " + serviceName + " not found in service registry");
                }

                ServiceInstance serviceInstance = instances.getFirst();
                String host = serviceInstance.getHost();
                int port = Integer.parseInt(serviceInstance.getMetadata().get("grpc-port"));
                log.info("Connecting to gRPC service {} at {}:{}", serviceName, host, port);

                // Create channel
                channel = ManagedChannelBuilder
                        .forAddress(host, port)
                        .usePlaintext()
                        .build();

                // Create stub using the channel
                UserGrpcServiceGrpc.UserGrpcServiceBlockingStub userGrpcStub = UserGrpcServiceGrpc.newBlockingStub(channel);

                log.info("Sending gRPC createUser request for email: {}", userDTO.getEmail());
                // Map UserDTO to UserRequest
                UserRequest request = UserRequest.newBuilder()
                        .setFirstName(userDTO.getFirstName() != null ? userDTO.getFirstName() : "")
                        .setLastName(userDTO.getLastName() != null ? userDTO.getLastName() : "")
                        .setEmail(userDTO.getEmail() != null ? userDTO.getEmail() : "")
                        .setUsername(userDTO.getUsername() != null ? userDTO.getUsername() : "")
                        .setPhone(userDTO.getPhone() != null ? userDTO.getPhone() : "")
                        .setProfileUrl(userDTO.getProfileUrl() != null ? userDTO.getProfileUrl() : "")
                        .setClinicId(userDTO.getClinicId() != null ? userDTO.getClinicId() : "")
                        .build();

                // Call the gRPC service
                UserResponse response = userGrpcStub.createUser(request);

                log.info("Received gRPC createUser response for email: {}", userDTO.getEmail());

                // Map the response back to UserDTO
                return UserDTO.builder()
                        .userId(response.getUserId())
                        .firstName(response.getFirstName())
                        .lastName(response.getLastName())
                        .email(response.getEmail())
                        .username(response.getUsername())
                        .phone(response.getPhone())
                        .profileUrl(response.getProfileUrl())
                        .clinicId(response.getClinicId())
                        .build();

            } catch (StatusRuntimeException e) {
                log.warn("gRPC status received while creating user for email: {}, status: {}",
                        userDTO.getEmail(), e.getStatus().getCode());

                // For ALREADY_EXISTS, return a user DTO with appropriate message instead of throwing exception
                if (e.getStatus().getCode() == io.grpc.Status.Code.ALREADY_EXISTS) {
                    log.info("User already exists with email: {}", userDTO.getEmail());
                    return UserDTO.builder()
                            .email(userDTO.getEmail())
                            .errorMessage("User already exists with email: " + userDTO.getEmail())
                            .errorCode(HttpStatus.CONFLICT.value())
                            .build();
                }

                // Map other gRPC status codes to appropriate UserServiceException
                switch (e.getStatus().getCode()) {
                    case NOT_FOUND:
                        throw new ResourceNotFoundException("Resource not found: " + e.getStatus().getDescription(),
                                HttpStatus.NOT_FOUND.value());
                    case INVALID_ARGUMENT:
                        throw new BadArgumentException("Invalid user data: " + e.getStatus().getDescription(),
                                HttpStatus.BAD_REQUEST.value());
                    default:
                        throw new UserServiceException("User service error: " + e.getStatus().getDescription(),
                                HttpStatus.INTERNAL_SERVER_ERROR.value());
                }
            } catch (Exception e) {
                log.error("Unexpected error in gRPC communication", e);
                throw new UserServiceException("Failed to create user via gRPC: " + e.getMessage(),
                        HttpStatus.INTERNAL_SERVER_ERROR.value());
            } finally {
                // Properly shutdown the channel
                shutdownChannel(channel);
            }
        });
    }

    // Fallback method for createUser
    // Fallback method for createUser
    public CompletableFuture<UserDTO> createUserFallback(UserDTO userDTO, Exception e) {
        if (e instanceof UserAlreadyExistsException) {
            log.warn("User already exists with email: {}", userDTO.getEmail());
            CompletableFuture<UserDTO> future = new CompletableFuture<>();
            future.completeExceptionally(new UserAlreadyExistsException(
                "User already exists with email: " + userDTO.getEmail(),
                HttpStatus.CONFLICT.value()));
            return future;
        }

        log.warn("Fallback: Unable to create user for email {}, using fallback", userDTO.getEmail(), e);
        CompletableFuture<UserDTO> future = new CompletableFuture<>();
        future.completeExceptionally(new UserServiceException("User service temporarily unavailable",
                HttpStatus.SERVICE_UNAVAILABLE.value()));
        return future;
    }

    /**
     * Safely shuts down a gRPC channel
     */
    private void shutdownChannel(ManagedChannel channel) {
        if (channel != null && !channel.isShutdown()) {
            try {
                log.debug("Shutting down gRPC channel");
                channel.shutdown();
                // Wait for channel to terminate
                if (!channel.awaitTermination(SHUTDOWN_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                    log.warn("gRPC channel didn't shut down in time, forcing shutdown");
                    channel.shutdownNow();
                }
            } catch (InterruptedException e) {
                log.warn("Interrupted while shutting down gRPC channel", e);
                Thread.currentThread().interrupt(); // Restore the interrupted status
                channel.shutdownNow();
            }
        }
    }
}