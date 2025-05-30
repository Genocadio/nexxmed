package com.nexxserve.authservice.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceAuthorizationRequest {

    @NotNull(message = "Service authorizations map cannot be null")
    @Valid
    private Map<@NotEmpty(message = "Service name cannot be empty") String, ServiceRoles> serviceAuthorizations;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ServiceRoles {
        @NotNull(message = "Roles list cannot be null")
        @NotEmpty(message = "Roles list cannot be empty")
        private Set<@NotEmpty(message = "Role name cannot be empty") String> roles;
    }
}