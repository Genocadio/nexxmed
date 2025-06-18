package com.nexxserve.inventoryservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class StockDetails {
    private Integer quantity;
    private Integer originalQuantity;
    private BigDecimal unitPrice;
    private BigDecimal costPrice;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime entryDate;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime expirationDate;

    private String batchNumber;
    private SupplierInfo supplierInfo;
}