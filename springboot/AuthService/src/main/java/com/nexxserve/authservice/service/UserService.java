package com.nexxserve.authservice.service;

import com.nexxserve.authservice.dto.UserDTO;
import com.nexxserve.authservice.exception.UserServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;


@Service
public class UserService {

    private final WebClient webClient;
    @Autowired
    private DiscoveryClient discoveryClient;

    @Autowired
    public UserService(WebClient.Builder webClientBuilder, @Value("${user.service.url}") String userServiceUrl) {
        this.webClient = webClientBuilder
                .baseUrl(userServiceUrl)
                .build();
    }

    public UserDTO getUserById(String userId) {
        return webClient.get()
                .uri("/users/public/{userId}", userId)
                .retrieve()
                .bodyToMono(UserDTO.class)
                .block();
    }

    public UserDTO createUser(UserDTO userDTO) {
        try {
            ServiceInstance serviceInstance = discoveryClient.getInstances("users").getFirst();
            if (serviceInstance == null) {
                throw new UserServiceException("User service instance not found", HttpStatus.SERVICE_UNAVAILABLE.value());
            }

            return webClient.post()
                    .uri(serviceInstance.getUri() + "/users")
                    .bodyValue(userDTO)
                    .retrieve()
                    .bodyToMono(UserDTO.class)
                    .onErrorMap(this::handleWebClientError)
                    .block();
        } catch (Exception e) {
            if (e instanceof UserServiceException) {
                throw e;
            }
            throw new UserServiceException("Failed to create user: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    private Throwable handleWebClientError(Throwable ex) {
        if (ex instanceof WebClientResponseException webClientEx) {
            HttpStatus status = HttpStatus.valueOf(webClientEx.getStatusCode().value());
            String errorMessage = webClientEx.getResponseBodyAsString();

            // Handle specific error cases
            if (status == HttpStatus.CONFLICT) {
                return new UserServiceException("User already exists: " + errorMessage, status.value());
            } else if (status == HttpStatus.NOT_FOUND) {
                return new UserServiceException("User not found: " + errorMessage, status.value());
            } else if (status == HttpStatus.BAD_REQUEST) {
                return new UserServiceException("Invalid user data: " + errorMessage, status.value());
            }

            return new UserServiceException("User service error: " + errorMessage, status.value());
        }

        return new UserServiceException("User service communication error: " + ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
}