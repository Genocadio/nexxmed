package com.nexxserve.medicine.dto;

import com.nexxserve.medicine.enums.ApprovalType;
import com.nexxserve.medicine.enums.CoverageStatus;
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

//    // Medicine information
//    private UUID medicineId;
//    private String medicineName;
//    private String medicineType; // "GENERIC", "BRAND", or "VARIANT"
//
//    // If it's a brand or variant, include additional information
//    private UUID variantId;
//    private String variantName;
//    private UUID genericId;
//    private String genericName;

    // Coverage details
    private CoverageStatus status;
    private BigDecimal insurancePrice;
    private BigDecimal clientContributionPercentage;
    private BigDecimal insuranceCoveragePercentage;
    private Boolean requiresPreApproval;
    private ApprovalType approvalType;
    private BigDecimal maxCoverageAmount;
    private BigDecimal minClientContribution;
    private BigDecimal maxClientContribution;

    // Effective dates
    private LocalDateTime effectiveFrom;
    private LocalDateTime effectiveTo;

    // Additional information
    private String conditions;
    private String approvalNotes;

    // Audit information
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
    private Integer version;
}