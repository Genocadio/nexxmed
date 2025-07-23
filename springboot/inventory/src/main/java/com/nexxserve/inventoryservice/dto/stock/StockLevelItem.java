package com.nexxserve.inventoryservice.dto.stock;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nexxserve.inventoryservice.dto.ProductReference;
import com.nexxserve.inventoryservice.enums.StockStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class StockLevelItem {
    private UUID stockEntryId;
    private ProductReference productReference;
    private Integer currentQuantity;
    private Integer originalQuantity;
    private BigDecimal unitPrice;
    private StockStatus status;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime expirationDate;

    private String batchNumber;
    private boolean isLowStock;
    private boolean isExpiringSoon;
}