package com.nexxserve.inventoryservice.dto.sync;

import com.nexxserve.inventoryservice.entity.medicine.Brand;
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
    private UUID variantId; // Only store reference to variant
    private String brandName;
    private String manufacturer;
    private String country;
    private Instant createdAt;
    private Instant updatedAt;
    private Double syncVersion;

    public static BrandSyncData fromEntity(Brand entity) {
        return BrandSyncData.builder()
                .id(entity.getId())
                .variantId(entity.getVariant() != null ? entity.getVariant().getId() : null)
                .brandName(entity.getBrandName())
                .manufacturer(entity.getManufacturer())
                .country(entity.getCountry())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .syncVersion(entity.getSyncVersion())
                .build();
    }
    public Brand toEntity() {
        Brand brand = new Brand();
        brand.setId(this.id);
        brand.setBrandName(this.brandName);
        brand.setManufacturer(this.manufacturer);
        brand.setCountry(this.country);
        brand.setCreatedAt(this.createdAt);
        brand.setUpdatedAt(this.updatedAt);
        brand.setSyncVersion(this.syncVersion);

        return brand;
    }
}

