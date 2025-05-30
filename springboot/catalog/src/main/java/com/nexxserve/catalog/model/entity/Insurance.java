package com.nexxserve.catalog.model.entity;

import com.nexxserve.catalog.enums.InsuranceStatus;
import com.nexxserve.catalog.enums.ApprovalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "insurances")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Insurance {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, unique = true)
    private String code; // Insurance company code (e.g., "NHIF", "AAR", "JUBILEE")

    @Column(length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InsuranceStatus status;

    @Column(nullable = false)
    private String contactEmail;

    private String contactPhone;

    private String address;

    @Column(nullable = false)
    private Boolean requiresPreApproval = false;

    @Enumerated(EnumType.STRING)
    private ApprovalType defaultApprovalType;

    // Default client contribution percentage (can be overridden per product)
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal defaultClientContributionPercentage = BigDecimal.ZERO;

    // Maximum coverage amount for this insurance
    @Column(precision = 15, scale = 2)
    private BigDecimal maxCoverageAmount;

    @OneToMany(mappedBy = "insurance", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProductInsuranceCoverage> productCoverages;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private String createdBy;

    @Column(nullable = false)
    private String updatedBy;

    @Version
    private Integer version;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}