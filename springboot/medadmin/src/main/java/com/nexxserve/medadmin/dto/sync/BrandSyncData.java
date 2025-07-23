package com.nexxserve.medadmin.dto.sync;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BrandSyncData {
    private UUID id;
    private UUID variantId;
    private String brandName;
    private String manufacturer;
    private String country;
    private Instant createdAt;
    private Instant updatedAt;
    private Double syncVersion;
}
