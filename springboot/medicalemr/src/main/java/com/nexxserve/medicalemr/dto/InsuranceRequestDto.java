package com.nexxserve.medicalemr.dto;

import com.nexxserve.medicalemr.enums.*;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class InsuranceRequestDto {

    @NotNull(message = "Insurance provider is required")
    private String provider;

    @NotBlank(message = "Policy number is required")
    private String policyNumber;

    @NotNull(message = "Policy expiry date is required")
    @Future(message = "Policy expiry date must be in the future")
    private LocalDate policyExpiry;

    private boolean isPrimary;

    @NotNull(message = "Coverage type is required")
    private CoverageType coverageType;

    @NotNull(message = "Coverage percentage is required")
    private int coveragePercentage;
}