package com.nexxserve.inventoryservice.dto.sync;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UnifiedSyncRequest {
    private String deviceId;
    private Instant lastSyncTime;
    private List<TherapeuticClassSyncData> therapeuticClasses;
    private List<GenericSyncData> generics;
    private List<VariantSyncData> variants;
}

