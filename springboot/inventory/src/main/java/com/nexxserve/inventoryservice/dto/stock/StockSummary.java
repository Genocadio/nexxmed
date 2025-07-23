package com.nexxserve.inventoryservice.dto.stock;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class StockSummary {
    private int totalItems;
    private int lowStockItems;
    private int expiredItems;
    private int expiringSoonItems;
    private BigDecimal totalValue;
}