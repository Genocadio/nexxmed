package com.nexxserve.inventoryservice.dto;

import com.nexxserve.inventoryservice.enums.ProductType;
import com.nexxserve.inventoryservice.enums.SourceService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductReference {
    @NotBlank(message = "Reference ID is required")
    private String referenceId;

    @NotNull(message = "Product type is required")
    private ProductType productType;

    @NotNull(message = "Source service is required")
    private SourceService sourceService;
}
