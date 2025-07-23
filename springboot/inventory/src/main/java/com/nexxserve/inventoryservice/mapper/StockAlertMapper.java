package com.nexxserve.inventoryservice.mapper;

import com.nexxserve.inventoryservice.dto.ProductReference;
import com.nexxserve.inventoryservice.dto.stock.StockAlertResponse;
import com.nexxserve.inventoryservice.entity.inventory.StockAlert;
import com.nexxserve.inventoryservice.entity.inventory.StockEntry;
import com.nexxserve.inventoryservice.enums.AlertType;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class StockAlertMapper {

    public StockAlertResponse toResponse(StockAlert alert) {
        return StockAlertResponse.builder()
                .id(alert.getId())
                .stockEntryId(alert.getStockEntry().getId())
                .alertType(alert.getAlertType())
                .threshold(alert.getThreshold())
                .currentQuantity(alert.getCurrentQuantity())
                .alertDate(alert.getAlertDate())
                .status(alert.getStatus())
                .message(alert.getMessage())
                .productReference(ProductReference.builder()
                        .referenceId(alert.getStockEntry().getReferenceId())
                        .productType(alert.getStockEntry().getProductType())
                        .sourceService(alert.getStockEntry().getSourceService())
                        .build())
                .build();
    }

    // Method to create alerts automatically based on stock conditions
    public StockAlert createLowStockAlert(StockEntry stockEntry, int threshold) {
        return StockAlert.builder()
                .stockEntry(stockEntry)
                .alertType(AlertType.LOW_STOCK)
                .threshold(threshold)
                .currentQuantity(stockEntry.getQuantity())
                .alertDate(LocalDateTime.now())
                .message("Stock quantity below threshold of " + threshold)
                .build();
    }

    public StockAlert createExpiringSoonAlert(StockEntry stockEntry, int daysAhead) {
        return StockAlert.builder()
                .stockEntry(stockEntry)
                .alertType(AlertType.EXPIRING_SOON)
                .alertDate(LocalDateTime.now())
                .currentQuantity(stockEntry.getQuantity())
                .message("Stock expiring in " + daysAhead + " days")
                .build();
    }

    public StockAlert createExpiredAlert(StockEntry stockEntry) {
        return StockAlert.builder()
                .stockEntry(stockEntry)
                .alertType(AlertType.EXPIRED)
                .alertDate(LocalDateTime.now())
                .currentQuantity(stockEntry.getQuantity())
                .message("Stock has expired")
                .build();
    }
}