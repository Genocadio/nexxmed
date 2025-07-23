package com.nexxserve.inventoryservice.dto.Insurance;

import com.nexxserve.inventoryservice.entity.medicine.MedicineInsuranceCoverage;
import com.nexxserve.inventoryservice.enums.ApprovalType;
import com.nexxserve.inventoryservice.enums.CoverageStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InsuranceCoverageResponseDto {

    private UUID id;
    private UUID insuranceId;
    private String insuranceName;

    // Coverage details
    private CoverageStatus status;
    private BigDecimal insurancePrice;
    private BigDecimal clientContributionPercentage;
    private Boolean requiresPreApproval;

    // Audit information
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
    private Integer version;

    public static InsuranceCoverageResponseDto  toResponseDto(MedicineInsuranceCoverage medicineInsuranceCoverage) {
        if (medicineInsuranceCoverage == null) return null;
        return InsuranceCoverageResponseDto.builder()
                .id(medicineInsuranceCoverage.getId())
                .insuranceId(medicineInsuranceCoverage.getInsurance() != null ? medicineInsuranceCoverage.getInsurance().getId() : null)
                .insuranceName(medicineInsuranceCoverage.getInsurance() != null ? medicineInsuranceCoverage.getInsurance().getName() : null)
                .status(medicineInsuranceCoverage.getStatus())
                .insurancePrice(medicineInsuranceCoverage.getInsurancePrice())
                .clientContributionPercentage(medicineInsuranceCoverage.getClientContributionPercentage())
                .requiresPreApproval(medicineInsuranceCoverage.getRequiresPreApproval())
                .createdAt(medicineInsuranceCoverage.getCreatedAtAsLocalDateTime())
                .updatedAt(medicineInsuranceCoverage.getUpdatedAtAsLocalDateTime())
                .createdBy(medicineInsuranceCoverage.getCreatedBy())
                .updatedBy(medicineInsuranceCoverage.getUpdatedBy())
                .build();
    }
}