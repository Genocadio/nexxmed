package com.nexxserve.medicine.dto;

import com.nexxserve.medicine.enums.ApprovalType;
import com.nexxserve.medicine.enums.CoverageStatus;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
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
public class InsuranceCoverageRequestDto {

    @NotNull(message = "Insurance ID is required")
    private UUID insuranceId;

    // Only one of these should be provided
    private UUID genericId;
    private UUID brandId;
    private UUID variantId;

    @NotNull(message = "Status is required")
    private CoverageStatus status;

    private BigDecimal insurancePrice;

    @NotNull(message = "Client contribution percentage is required")
    @DecimalMin(value = "0.0", message = "Client contribution percentage must be positive")
    @DecimalMax(value = "100.0", message = "Client contribution percentage cannot exceed 100")
    private BigDecimal clientContributionPercentage;

    private Boolean requiresPreApproval;

    private ApprovalType approvalType;

    private BigDecimal maxCoverageAmount;

    private BigDecimal minClientContribution;

    private BigDecimal maxClientContribution;

    @NotNull(message = "Effective from date is required")
    private LocalDateTime effectiveFrom;

    private LocalDateTime effectiveTo;

    private String conditions;

    private String approvalNotes;
}