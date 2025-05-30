package com.nexxserve.catalog.dto;

import com.nexxserve.catalog.enums.ApprovalType;
import com.nexxserve.catalog.enums.InsuranceStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InsuranceRequestDto {
    @NotBlank(message = "Name is required")
    @Size(max = 255, message = "Name must be less than 255 characters")
    private String name;

    @NotBlank(message = "Code is required")
    @Size(max = 50, message = "Code must be less than 50 characters")
    private String code;

    @Size(max = 1000, message = "Description must be less than 1000 characters")
    private String description;

    @NotNull(message = "Status is required")
    private InsuranceStatus status;

    @NotBlank(message = "Contact email is required")
    @Email(message = "Invalid email format")
    private String contactEmail;

    private String contactPhone;
    private String address;
    private Boolean requiresPreApproval;
    private ApprovalType defaultApprovalType;
    private BigDecimal defaultClientContributionPercentage;
    private BigDecimal maxCoverageAmount;
}