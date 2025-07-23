package com.nexxserve.inventoryservice.dto.catalog;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductInsuranceCoverageResponseDto {

    private UUID id;

    private UUID insuranceId;

    private String insuranceName;

    // Product information
    private UUID productFamilyId;
    private String productFamilyName;
    private UUID productVariantId;
    private String productVariantName;


    private BigDecimal insurancePrice;

    private BigDecimal clientContributionPercentage;

    private Boolean requiresPreApproval;

    // Audit fields
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String createdBy;

    private String updatedBy;


    // Helper methods
    public boolean isProductFamilyCoverage() {
        return productFamilyId != null;
    }

    public boolean isProductVariantCoverage() {
        return productVariantId != null;
    }

    public String getProductName() {
        if (productFamilyName != null) {
            return productFamilyName;
        } else if (productVariantName != null) {
            return productVariantName;
        }
        return null;
    }

    public UUID getProductId() {
        if (productFamilyId != null) {
            return productFamilyId;
        } else if (productVariantId != null) {
            return productVariantId;
        }
        return null;
    }
}