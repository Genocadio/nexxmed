package com.nexxserve.medadmin.dto.sync;


import com.nexxserve.medadmin.entity.medicine.MedicineInsuranceCoverage;
import com.nexxserve.medadmin.enums.CoverageStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicineInsuranceCoverageSyncData {
    private UUID id;
    private UUID insuranceId;
    private UUID genericId;
    private UUID brandId;
    private UUID variantId;
    private CoverageStatus status;
    private BigDecimal insurancePrice;
    private BigDecimal clientContributionPercentage;
    private Boolean requiresPreApproval;
    private String createdBy;
    private String updatedBy;
    private Instant createdAt;
    private Instant updatedAt;
    private Double syncVersion;
    public static MedicineInsuranceCoverageSyncData fromEntity(MedicineInsuranceCoverage entity) {
        return MedicineInsuranceCoverageSyncData.builder()
                .id(entity.getId())
                .insuranceId(entity.getInsurance() != null ? entity.getInsurance().getId() : null)
                .genericId(entity.getGeneric() != null ? entity.getGeneric().getId() : null)
                .brandId(entity.getBrand() != null ? entity.getBrand().getId() : null)
                .variantId(entity.getVariant() != null ? entity.getVariant().getId() : null)
                .status(entity.getStatus())
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


