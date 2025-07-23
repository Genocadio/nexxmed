package com.nexxserve.inventoryservice.dto.stock;

import com.nexxserve.inventoryservice.dto.ProductReference;
import com.nexxserve.inventoryservice.dto.medicine.*;
import com.nexxserve.inventoryservice.enums.StockStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
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