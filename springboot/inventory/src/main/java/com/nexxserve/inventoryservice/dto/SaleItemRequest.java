package com.nexxserve.inventoryservice.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class SaleItemRequest {
    @NotNull(message = "Stock entry ID is required")
    private UUID stockEntryId;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be positive")
    private Integer quantity;

    @DecimalMin(value = "0.01", message = "Unit price must be positive")
    private BigDecimal unitPrice; // Optional - will use stock entry price if not provided
}
