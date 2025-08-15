package com.nexxserve.medadmin.dto.sync;

import com.nexxserve.medadmin.entity.medicine.Generic;
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

    // Parent relationship - only IDs to avoid nested objects
    private UUID therapeuticClassId;

    public static GenericSyncData fromEntity(Generic entity) {
        return GenericSyncData.builder()
                .id(entity.getId())
                .name(entity.getName())
                .chemicalName(entity.getChemicalName())
                .description(entity.getDescription())
                .isParent(entity.getIsParent())
                .syncVersion(entity.getSyncVersion())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .therapeuticClassId(entity.getTherapeuticClass() != null ? entity.getTherapeuticClass().getId() : null)
                .build();
    }
    public Generic toEntity() {
        Generic generic = new Generic();
        generic.setId(this.id);
        generic.setName(this.name);
        generic.setChemicalName(this.chemicalName);
        generic.setDescription(this.description);
        generic.setIsParent(this.isParent);
        generic.setSyncVersion(this.syncVersion);
        generic.setCreatedAt(this.createdAt);
        generic.setUpdatedAt(this.updatedAt);

        return generic;
    }
}