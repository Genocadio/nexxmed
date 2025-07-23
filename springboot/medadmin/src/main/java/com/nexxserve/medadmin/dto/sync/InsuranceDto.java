package com.nexxserve.medadmin.dto.sync;

import lombok.Data;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class InsuranceDto {
    private UUID id;
    private String name;
    private String abbreviation;
    private BigDecimal defaultClientContributionPercentage;
    private Boolean defaultRequiresPreApproval;
    private Boolean active;
    private String createdBy;
    private String updatedBy;
    private Instant updatedAt;
    private Instant createdAt;
    private Integer version;
    private Double syncVersion;
    private boolean syncOperation;
    private LocalDateTime updatedAtAsLocalDateTime;
    private LocalDateTime createdAtAsLocalDateTime;
}