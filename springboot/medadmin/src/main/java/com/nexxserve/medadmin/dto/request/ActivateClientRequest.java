package com.nexxserve.medadmin.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActivateClientRequest {
    @NotNull(message = "Client ID is required")
    private String clientId;
    @NotNull(message = "Password is required")
    private String password;
    @NotNull(message = "Email or phone is required")
    private String emailOrPhone;
}