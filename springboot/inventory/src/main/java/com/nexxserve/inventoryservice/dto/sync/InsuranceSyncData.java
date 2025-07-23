package com.nexxserve.inventoryservice.dto.sync;

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
public class InsuranceSyncData {
    private UUID id;
    private String name;
    private String abbreviation;
    private BigDecimal defaultClientContributionPercentage;
    private Boolean defaultRequiresPreApproval;
    private Boolean active;
    private String createdBy;
    private String updatedBy;
    private Instant createdAt;
    private Instant updatedAt;
    private Double syncVersion;
}