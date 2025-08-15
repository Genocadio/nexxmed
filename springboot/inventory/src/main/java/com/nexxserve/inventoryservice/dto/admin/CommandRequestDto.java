package com.nexxserve.inventoryservice.dto.admin;
import lombok.Data;

@Data
public class CommandRequestDto {
    private String command;
    private String deviceId;
    private String payload; // Optional: for additional command parameters
}