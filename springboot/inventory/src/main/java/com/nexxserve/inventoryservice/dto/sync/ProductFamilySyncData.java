package com.nexxserve.inventoryservice.dto.sync;

import com.nexxserve.inventoryservice.entity.catalog.ProductFamily;
import com.nexxserve.inventoryservice.enums.HazardClass;
import com.nexxserve.inventoryservice.enums.LifecycleStage;
import com.nexxserve.inventoryservice.enums.ProductStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductFamilySyncData {
    // Core fields
    private UUID id;
    private String name;
    private String description;
    private UUID categoryId;
    private String brand;
    private String createdBy;
    private String updatedBy;

    // Sync-specific fields
    private Instant createdAt;
    private Instant updatedAt;
    private Double syncVersion;

    public static ProductFamilySyncData fromEntity(ProductFamily entity) {
        return ProductFamilySyncData.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .categoryId(entity.getCategory() != null ? entity.getCategory().getId() : null)
                .brand(entity.getBrand())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .syncVersion(entity.getSyncVersion())
                .build();
    }
}
