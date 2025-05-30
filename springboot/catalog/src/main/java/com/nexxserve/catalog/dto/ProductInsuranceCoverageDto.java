package com.nexxserve.catalog.dto;

import com.nexxserve.catalog.enums.ApprovalType;
import com.nexxserve.catalog.enums.CoverageStatus;
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
public class ProductInsuranceCoverageDto {
    private UUID id;
    private UUID insuranceId;
    private String insuranceName;
    private UUID productFamilyId;
    private String productFamilyName;
    private UUID productVariantId;
    private String productVariantName;
    private CoverageStatus status;
    private BigDecimal insurancePrice;
    private BigDecimal clientContributionPercentage;
    private BigDecimal insuranceCoveragePercentage;
    private Boolean requiresPreApproval;
    private ApprovalType approvalType;
    private BigDecimal maxCoverageAmount;
    private BigDecimal minClientContribution;
    private BigDecimal maxClientContribution;
    private LocalDateTime effectiveFrom;
    private LocalDateTime effectiveTo;
    private String conditions;
    private String approvalNotes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}