package com.nexxserve.inventoryservice.dto.sync;

import com.nexxserve.inventoryservice.entity.catalog.ProductInsuranceCoverage;
import com.nexxserve.inventoryservice.enums.ApprovalType;
import com.nexxserve.inventoryservice.enums.CoverageStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductInsuranceCoverageSyncData {
    private UUID id;
    private UUID insuranceId;
    private UUID productFamilyId;
    private UUID productVariantId;
    private BigDecimal insurancePrice;
    private BigDecimal clientContributionPercentage;
    private Boolean requiresPreApproval;
    private String createdBy;
    private String updatedBy;

    // Sync-specific fields
    private Instant createdAt;
    private Instant updatedAt;
    private Double syncVersion;

    public static ProductInsuranceCoverageSyncData fromEntity(ProductInsuranceCoverage entity) {
        return ProductInsuranceCoverageSyncData.builder()
                .id(entity.getId())
                .insuranceId(entity.getInsurance() != null ? entity.getInsurance().getId() : null)
                .productFamilyId(entity.getProductFamily() != null ? entity.getProductFamily().getId() : null)
                .productVariantId(entity.getProductVariant() != null ? entity.getProductVariant().getId() : null)
                .insurancePrice(entity.getInsurancePrice())
                .clientContributionPercentage(entity.getClientContributionPercentage())
                .requiresPreApproval(entity.getRequiresPreApproval())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .syncVersion(entity.getSyncVersion())
                .build();
    }
}

