package com.nexxserve.inventoryservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class ExpiryItem {
    private UUID stockEntryId;
    private ProductReference productReference;
    private Integer quantity;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime expirationDate;

    private String batchNumber;
    private BigDecimal unitPrice;
    private BigDecimal totalValue;
    private int daysUntilExpiry;
}
