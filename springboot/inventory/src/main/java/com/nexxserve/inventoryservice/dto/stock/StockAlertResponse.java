package com.nexxserve.inventoryservice.dto.stock;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nexxserve.inventoryservice.dto.ProductReference;
import com.nexxserve.inventoryservice.enums.AlertStatus;
import com.nexxserve.inventoryservice.enums.AlertType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class StockAlertResponse {
    private UUID id;
    private UUID stockEntryId;
    private AlertType alertType;
    private Integer threshold;
    private Integer currentQuantity;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime alertDate;

    private AlertStatus status;
    private String message;
    private ProductReference productReference;
}
