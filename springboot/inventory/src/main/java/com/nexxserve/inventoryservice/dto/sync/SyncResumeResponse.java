package com.nexxserve.inventoryservice.dto.sync;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SyncResumeResponse {
    private UUID sessionId;
    private String deviceId;
    private String status; // "SUCCESS" or "FAILED"
    private Map<String, Object> processedData; // Results of processing received data
    private String message;
}

