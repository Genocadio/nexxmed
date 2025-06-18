package com.nexxserve.inventoryservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SupplierInfo {
    @NotBlank(message = "Supplier ID is required")
    private String supplierId;

    @NotBlank(message = "Supplier name is required")
    private String supplierName;
}
