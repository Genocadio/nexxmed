package com.nexxserve.inventoryservice.dto;

import java.time.Instant;
import lombok.Data;
@Data
public class Activationresponse {
    private boolean activated;
    private Instant tokenSavedAt;
    private String serverUrl;
    private String message;
}