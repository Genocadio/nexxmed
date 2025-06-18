package com.nexxserve.medicine.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InsuranceRequestDto {
    private String name;
    private String abbreviation;
    private BigDecimal defaultClientContributionPercentage;
    private Boolean defaultRequiresPreApproval;
    private Boolean active;
}