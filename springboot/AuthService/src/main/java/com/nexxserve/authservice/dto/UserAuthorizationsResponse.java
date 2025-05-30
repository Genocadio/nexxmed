package com.nexxserve.authservice.dto;

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
public class UserAuthorizationsResponse {
    private String userId;
    private Map<String, AuthorizationDetails> serviceAuthorizations;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AuthorizationDetails {
        private Set<String> roles;
        private Set<String> permissions;
    }
}