package com.nexxserve.medadmin.dto.sync;

import com.nexxserve.medadmin.entity.catalog.ProductFamily;
import com.nexxserve.medadmin.entity.catalog.ProductVariant;
import com.nexxserve.medadmin.entity.catalog.valueobjects.ProductDimensions;
import com.nexxserve.medadmin.entity.catalog.valueobjects.ProductWeight;
import com.nexxserve.medadmin.enums.UnitOfMeasure;
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
public class ProductVariantSyncData {
    // Core fields
    private UUID id;
    private UUID familyId;
    private String name;
    private String sku;
    private String brand;
    private String country;
    private ProductDimensions dimensions;
    private ProductWeight weight;
    private String color;
    private UnitOfMeasure unitOfMeasure;
    private Map<String, String> specifications;
    private String createdBy;
    private String updatedBy;

    // Sync-specific fields
    private Instant createdAt;
    private Instant updatedAt;
    private Double syncVersion;
    public static  ProductVariantSyncData fromEntity(ProductVariant entity) {
        return ProductVariantSyncData.builder()
                .id(entity.getId())
                .familyId(entity.getFamily() != null ? entity.getFamily().getId() : null)
                .name(entity.getName())
                .sku(entity.getSku())
                .brand(entity.getBrand())
                .country(entity.getCountry())
                .dimensions(entity.getDimensions())
                .weight(entity.getWeight())
                .color(entity.getColor())
                .unitOfMeasure(entity.getUnitOfMeasure())
                .specifications(entity.getSpecifications())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .syncVersion(entity.getSyncVersion())
                .build();
    }
}

