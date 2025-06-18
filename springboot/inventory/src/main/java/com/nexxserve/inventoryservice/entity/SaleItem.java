package com.nexxserve.inventoryservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "sale_items")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaleItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sale_transaction_id", nullable = false)
    @ToString.Exclude
    private SaleTransaction saleTransaction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_entry_id", nullable = false)
    private StockEntry stockEntry;

    @Column(name = "quantity_sold", nullable = false)
    private Integer quantitySold;

    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    // Individual item insurance details
    @Column(name = "insurance_coverage")
    private Integer insuranceCoverage;

    @Column(name = "insurance_payment", precision = 10, scale = 2)
    private BigDecimal insurancePayment;

    @Column(name = "patient_payment", precision = 10, scale = 2)
    private BigDecimal patientPayment;

    @PrePersist
    @PreUpdate
    public void calculateTotalAmount() {
        if (quantitySold != null && unitPrice != null) {
            this.totalAmount = unitPrice.multiply(BigDecimal.valueOf(quantitySold));
        }
    }
}