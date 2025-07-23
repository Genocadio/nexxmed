package com.nexxserve.inventoryservice.dto.sync;

import com.nexxserve.inventoryservice.entity.catalog.CategoryReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryReferenceSyncData {
    // Fields directly mapped to CategoryReference entity
    private UUID id;
    private String name;
    private String code;
    private UUID parentId;
    private String description;
    // Sync-specific fields
    private Instant createdAt;
    private Instant updatedAt;
    private Double syncVersion;
    public static CategoryReferenceSyncData fromEntity(CategoryReference entity) {
        return CategoryReferenceSyncData.builder()
                .id(entity.getId())
                .name(entity.getName())
                .code(entity.getCode())
                .parentId(entity.getParent() != null ? entity.getParent().getId() : null)
                .description(entity.getDescription())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .syncVersion(entity.getSyncVersion())
                .build();
    }
}
