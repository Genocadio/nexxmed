package com.nexxserve.medadmin.entity.inventory;


import com.nexxserve.medadmin.entity.clients.Client;
import com.nexxserve.medadmin.enums.ProductType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventory_reports")
@Getter
@Setter
public class InventoryReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String transactionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @Column(nullable = false)
    private ProductType productType; // "BRANDS", "VARIANTS", "PRODUCT_VARIANTS"

    @Column(nullable = false)
    private String productId;

    @Column(nullable = false)
    private String action; // "ADDITION" or "SALE"

    @Column(nullable = false)
    private int quantity;

    private Double buyingPrice; // for addition

    private Double sellingPrice;

    private String supplierName; // optional

    private String insuranceId; // optional, for sale

    private String insuranceName; // optional

    private Double coveragePercentage; // optional

    private String doneBy; // User who performed the action

    private LocalDateTime doneAt;

    private LocalDateTime expirationDate; // optional, for products with expiration
}