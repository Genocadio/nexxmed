package com.nexxserve.catalog.dto;

import com.nexxserve.catalog.enums.ApprovalType;
import com.nexxserve.catalog.enums.InsuranceStatus;
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
public class InsuranceDto {
    private UUID id;
    private String name;
    private String code;
    private String description;
    private InsuranceStatus status;
    private String contactEmail;
    private String contactPhone;
    private String address;
    private Boolean requiresPreApproval;
    private ApprovalType defaultApprovalType;
    private BigDecimal defaultClientContributionPercentage;
    private BigDecimal maxCoverageAmount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
    private Integer version;
}