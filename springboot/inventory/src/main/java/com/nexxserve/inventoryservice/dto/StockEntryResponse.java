package com.nexxserve.inventoryservice.dto;

import com.nexxserve.inventoryservice.enums.StockStatus;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class StockEntryResponse {
    private UUID id;
    private ProductReference productReference;
    private StockDetails stockDetails;
    private StockStatus status;
    private StockMetadata metadata;
    private EnrichedProductData productData;
}