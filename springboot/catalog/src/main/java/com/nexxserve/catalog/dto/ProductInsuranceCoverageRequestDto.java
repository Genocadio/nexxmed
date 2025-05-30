package com.nexxserve.catalog.dto;

import com.nexxserve.catalog.enums.ApprovalType;
import com.nexxserve.catalog.enums.CoverageStatus;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductInsuranceCoverageRequestDto {
    @NotNull(message = "Coverage status is required")
    private CoverageStatus status;

    private BigDecimal insurancePrice;

    @NotNull(message = "Client contribution percentage is required")
    @DecimalMin(value = "0.0", message = "Client contribution percentage must be at least 0")
    @DecimalMax(value = "100.0", message = "Client contribution percentage must be at most 100")
    private BigDecimal clientContributionPercentage;

    private Boolean requiresPreApproval;
    private ApprovalType approvalType;
    private BigDecimal maxCoverageAmount;
    private BigDecimal minClientContribution;
    private BigDecimal maxClientContribution;

    @NotNull(message = "Effective from date is required")
    private LocalDateTime effectiveFrom;

    private LocalDateTime effectiveTo;

    @Size(max = 1000, message = "Conditions must be less than 1000 characters")
    private String conditions;

    @Size(max = 500, message = "Approval notes must be less than 500 characters")
    private String approvalNotes;
}