package com.nexxserve.inventoryservice.dto.Insurance;

import com.nexxserve.inventoryservice.enums.CoverageStatus;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
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
public class InsuranceCoverageRequestDto {

    @NotNull(message = "Insurance ID is required")
    private UUID insuranceId;

    // Only one of these should be provided
    private UUID genericId;
    private UUID brandId;
    private UUID variantId;

    @NotNull(message = "Status is required")
    private CoverageStatus status;
    private BigDecimal insurancePrice;
    @NotNull(message = "Client contribution percentage is required")
    @DecimalMin(value = "0.0", message = "Client contribution percentage must be positive")
    @DecimalMax(value = "100.0", message = "Client contribution percentage cannot exceed 100")
    private BigDecimal clientContributionPercentage;

    private Boolean requiresPreApproval;

}