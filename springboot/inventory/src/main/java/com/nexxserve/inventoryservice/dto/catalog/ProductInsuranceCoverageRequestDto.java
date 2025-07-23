package com.nexxserve.inventoryservice.dto.catalog;

import com.nexxserve.inventoryservice.enums.ApprovalType;
import com.nexxserve.inventoryservice.enums.CoverageStatus;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class ProductInsuranceCoverageRequestDto {

    @NotNull(message = "Insurance ID is required")
    private UUID insuranceId;

    // Only one of these should be provided
    private UUID productFamilyId;
    private UUID productVariantId;


    private BigDecimal insurancePrice;

    @NotNull(message = "Client contribution percentage is required")
    @DecimalMin(value = "0.0", message = "Client contribution percentage must be at least 0")
    @DecimalMax(value = "100.0", message = "Client contribution percentage must be at most 100")
    private BigDecimal clientContributionPercentage;

    private Boolean requiresPreApproval;

}