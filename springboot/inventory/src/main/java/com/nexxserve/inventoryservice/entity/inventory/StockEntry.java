package com.nexxserve.inventoryservice.entity.inventory;


import com.nexxserve.inventoryservice.entity.BaseEntity;
import com.nexxserve.inventoryservice.enums.ProductType;
import com.nexxserve.inventoryservice.enums.SourceService;
import com.nexxserve.inventoryservice.enums.StockStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "stock_entries")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockEntry extends BaseEntity {


    // Add to StockEntry.java
    @Column(name = "product_name")
    private String productName;

    @Column(name = "product_description", length = 1000)
    private String productDescription;

    // Product Reference
    @Column(name = "reference_id", nullable = false)
    private String referenceId;

    @Enumerated(EnumType.STRING)
    @Column(name = "product_type", nullable = false)
    private ProductType productType;

    @Enumerated(EnumType.STRING)
    @Column(name = "source_service", nullable = false)
    private SourceService sourceService;

    // Stock Details
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "original_quantity", nullable = false)
    private Integer originalQuantity;

    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "cost_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal costPrice;

    @Column(name = "entry_date", nullable = false)
    private LocalDateTime entryDate;

    @Column(name = "expiration_date")
    private LocalDateTime expirationDate;

    @Column(name = "batch_number")
    private String batchNumber;

    // Supplier Information
    @Column(name = "supplier_id")
    private String supplierId;

    @Column(name = "supplier_name")
    private String supplierName;

    // Status
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private StockStatus status = StockStatus.ACTIVE;


    @Column(name = "created_by")
    private String createdBy;

    public void onCreate() {
        super.onCreate();
        if (entryDate == null) {
            entryDate = LocalDateTime.now();
        }
        if (originalQuantity == null) {
            originalQuantity = quantity;
        }
    }

    public boolean isExpired() {
        return expirationDate != null && expirationDate.isBefore(LocalDateTime.now());
    }

    public boolean isExpiringSoon(int daysAhead) {
        return expirationDate != null &&
                expirationDate.isBefore(LocalDateTime.now().plusDays(daysAhead));
    }

    public boolean isSoldOut() {
        return quantity != null && quantity <= 0;
    }

    public void reduceQuantity(int amount) {
        if (quantity < amount) {
            throw new IllegalArgumentException("Cannot reduce quantity below zero");
        }
        this.quantity -= amount;
        if (this.quantity == 0) {
            this.status = StockStatus.SOLD_OUT;
        }
    }

    public void increaseQuantity(int amount) {
        this.quantity += amount;
        if (this.status == StockStatus.SOLD_OUT && this.quantity > 0) {
            this.status = StockStatus.ACTIVE;
        }
    }
}