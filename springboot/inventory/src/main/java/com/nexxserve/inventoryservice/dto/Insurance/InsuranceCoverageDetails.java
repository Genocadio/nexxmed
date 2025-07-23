package com.nexxserve.inventoryservice.dto.Insurance;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class InsuranceCoverageDetails {
    private String id;
    private String insuranceId;
    private String insuranceName;
    private String productFamilyId;
    private String productFamilyName;
    private String productVariantId;
    private String productVariantName;
    private String status;
    private Double insurancePrice;
    private Double clientContributionPercentage;
    private Double insuranceCoveragePercentage;
    private Boolean requiresPreApproval;
    private String approvalType;
    private Double maxCoverageAmount;
    private Double minClientContribution;
    private Double maxClientContribution;
    private LocalDateTime effectiveFrom;
    private LocalDateTime effectiveTo;
    private String conditions;
    private String approvalNotes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
