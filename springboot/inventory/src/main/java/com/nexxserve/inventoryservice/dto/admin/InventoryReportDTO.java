package com.nexxserve.inventoryservice.dto.admin;

import com.nexxserve.inventoryservice.enums.ProductType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class InventoryReportDTO {
    private String transactionId; // Unique ID for sale or addition
    private ProductType productType; // "BRANDS", "VARIANTS", "PRODUCT_VARIANTS"
    private String productId;
    private String action; // "ADDITION" or "SALE"
    private int quantity;
    private Double buyingPrice; // for addition
    private Double sellingPrice;
    private String supplierName; // optional
    private String insuranceId; // optional, for sale
    private String insuranceName; // optional
    private Integer coveragePercentage; // optional
    private String doneBy; // User who performed the action
    private LocalDateTime doneAt;
    private LocalDateTime expirationDate; // optional, for products with expiration
}
