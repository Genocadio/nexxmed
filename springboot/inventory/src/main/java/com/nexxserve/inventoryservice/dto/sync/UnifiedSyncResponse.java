package com.nexxserve.inventoryservice.dto.sync;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UnifiedSyncResponse {
    private UUID syncSessionId;
    private String status;
    private Map<String, Object> data;
    private List<String> completedDataTypes;
    private Integer totalDataTypes;
    private Boolean isComplete;
    private String errorMessage;
}
