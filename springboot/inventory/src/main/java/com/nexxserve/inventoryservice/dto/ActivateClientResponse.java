package com.nexxserve.inventoryservice.dto;

import lombok.Data;

@Data
public class ActivateClientResponse {
    private String clientName;
    private String phoneNumber;
    private String email;
    private String token;
    private String refreshToken;
    private String message;
}