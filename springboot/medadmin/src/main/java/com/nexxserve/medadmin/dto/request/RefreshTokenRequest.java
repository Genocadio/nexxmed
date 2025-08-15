package com.nexxserve.medadmin.dto.request;


import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefreshTokenRequest {
    @NotNull(message = "Client ID is required")
    private String clientId;
}
