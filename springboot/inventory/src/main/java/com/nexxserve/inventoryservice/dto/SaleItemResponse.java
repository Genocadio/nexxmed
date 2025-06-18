package com.nexxserve.inventoryservice.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class SaleItemResponse {
    private UUID id;
    private UUID stockEntryId;
    private Integer quantitySold;
    private BigDecimal unitPrice;
    private BigDecimal totalAmount;
    private String productName; // Populated from external service
}