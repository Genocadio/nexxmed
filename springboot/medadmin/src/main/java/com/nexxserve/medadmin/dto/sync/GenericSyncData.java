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
public class GenericSyncData {
    private UUID id;
    private String name;
    private String chemicalName;
    private String description;
    private Boolean isParent;
    private Double syncVersion;
    private Instant createdAt;
    private Instant updatedAt;
    private UUID therapeuticClassId;
}