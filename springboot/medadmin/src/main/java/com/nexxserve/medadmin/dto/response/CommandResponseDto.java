package com.nexxserve.medadmin.dto.response;

import lombok.Data;

@Data
public class CommandResponseDto {
    private boolean success;
    private String message;
    private String clientResponse; // Optional: response from client
}