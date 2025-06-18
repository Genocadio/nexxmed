package com.nexxserve.inventoryservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "sale_transactions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaleTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime transactionDate;

    // Patient information
    @Column(name = "patient_name")
    private String patientName;

    @Column(name = "patient_contact")
    private String patientContact;

    // Insurance information
    @Column(name = "insurance_id")
    private UUID insuranceId;

    @Column(name = "insurance_name")
    private String insuranceName;

    @Column(name = "insurance_coverage")
    private Integer insuranceCoverage;

    @Column(name = "insurance_card_number")
    private String insuranceCardNumber;

    @Column(name = "principal_member_name")
    private String principalMemberName;

    // Prescriber information
    @Column(name = "prescriber_name")
    private String prescriberName;

    @Column(name = "prescriber_organization")
    private String prescriberOrganization;

    @Column(name = "prescriber_contact")
    private String prescriberContact;

    @Column(name = "prescriber_type")
    private String prescriberType;

    // Transaction totals
    @Column(name = "total_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice;

    @Column(name = "total_insurance_payment", precision = 10, scale = 2)
    private BigDecimal totalInsurancePayment;

    @Column(name = "total_patient_payment", precision = 10, scale = 2)
    private BigDecimal totalPatientPayment;

    @Column(name = "payment_mode")
    private String paymentMode;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @OneToMany(mappedBy = "saleTransaction", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SaleItem> items = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        if (transactionDate == null) {
            transactionDate = LocalDateTime.now();
        }
    }

    // Helper method to add items
    public void addItem(SaleItem item) {
        items.add(item);
        item.setSaleTransaction(this);
    }

    // Helper method to remove items
    public void removeItem(SaleItem item) {
        items.remove(item);
        item.setSaleTransaction(null);
    }
}