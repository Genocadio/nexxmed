package com.nexxserve.medadmin.dto.request;

import com.nexxserve.medadmin.entity.inventory.InventoryReport;
import com.nexxserve.medadmin.enums.ProductType;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
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
    private Double coveragePercentage; // optional
    private String doneBy; // User who performed the action
    private LocalDateTime  doneAt;
    private LocalDateTime expirationDate;
    public InventoryReport toEntity() {
        InventoryReport report = new InventoryReport();
        report.setTransactionId(this.transactionId);
        report.setProductType(this.productType);
        report.setProductId(this.productId);
        report.setAction(this.action);
        report.setQuantity(this.quantity);
        report.setBuyingPrice(this.buyingPrice);
        report.setSellingPrice(this.sellingPrice);
        report.setSupplierName(this.supplierName);
        report.setInsuranceId(this.insuranceId);
        report.setInsuranceName(this.insuranceName);
        report.setCoveragePercentage(this.coveragePercentage);
        report.setDoneBy(this.doneBy);
        report.setDoneAt(this.doneAt);
        report.setExpirationDate(this.expirationDate);
        return report;

    }
}