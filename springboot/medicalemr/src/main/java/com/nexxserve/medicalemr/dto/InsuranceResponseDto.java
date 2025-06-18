package com.nexxserve.medicalemr.dto;

import com.nexxserve.medicalemr.enums.CoverageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InsuranceResponseDto {
    private String id;
    private String patientIdentifier;
    private String provider;
    private String policyNumber;
    private LocalDate policyExpiry;
    private boolean isPrimary;
    private CoverageType coverageType;
    private Integer coveragePercentage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}