package com.nexxserve.medadmin.dto.sync;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

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

