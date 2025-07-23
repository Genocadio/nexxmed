package com.nexxserve.medadmin.dto.sync;

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
public class VariantSyncData {
    private UUID id;
    private String name;
    private String tradeName;
    private String form;
    private String route;
    private String strength;
    private String concentration;
    private String packaging;
    private String notes;
    private Double syncVersion;
    private Instant createdAt;
    private Instant updatedAt;
    private List<UUID> genericIds;
}