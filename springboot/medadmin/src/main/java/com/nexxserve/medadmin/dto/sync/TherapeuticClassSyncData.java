package com.nexxserve.medadmin.dto.sync;

import com.nexxserve.medadmin.entity.medicine.TherapeuticClass;
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
public class TherapeuticClassSyncData {
    private UUID id;
    private String name;
    private String description;
    private Instant createdAt;
    private Instant updatedAt;
    private Double syncVersion;

    public  static TherapeuticClassSyncData fromEntity(TherapeuticClass entity) {
        if (entity == null) return null;

        return TherapeuticClassSyncData.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .syncVersion(entity.getSyncVersion())
                .build();
    }
    public TherapeuticClass toEntity() {
        TherapeuticClass entity = new TherapeuticClass();
        entity.setId(this.id);
        entity.setName(this.name);
        entity.setDescription(this.description);
        entity.setCreatedAt(this.createdAt);
        entity.setUpdatedAt(this.updatedAt);
        entity.setSyncVersion(this.syncVersion);
        return entity;
    }
}

