package com.nexxserve.inventoryservice.dto.stock;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class StockLevelReport {
    private List<StockLevelItem> items;
    private StockSummary summary;
}
