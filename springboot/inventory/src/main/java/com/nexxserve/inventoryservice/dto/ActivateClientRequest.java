package com.nexxserve.inventoryservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ActivateClientRequest {
    @NotNull(message = "Client ID is required")
    private String clientId;
    @NotNull(message = "Password is required")
    private String password;
    @NotNull(message = "Email or phone is required")
    private String emailOrPhone;
    @NotNull(message = "Server URL is required")
    private String serverUrl;
}