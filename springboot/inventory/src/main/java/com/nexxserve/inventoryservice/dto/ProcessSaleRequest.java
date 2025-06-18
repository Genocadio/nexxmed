package com.nexxserve.inventoryservice.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ProcessSaleRequest {
    @NotEmpty(message = "Sale items cannot be empty")
    @Valid
    private List<SaleItemRequest> items;

    @NotBlank(message = "Cashier is required")
    private String cashier;

    @Valid
    private CustomerInfo customer;
}
