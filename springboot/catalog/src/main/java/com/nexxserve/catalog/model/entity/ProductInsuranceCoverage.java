package com.nexxserve.catalog.model.entity;

import com.nexxserve.catalog.enums.CoverageStatus;
import com.nexxserve.catalog.enums.ApprovalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "product_insurance_coverages",
        uniqueConstraints = @UniqueConstraint(columnNames = {"insurance_id", "product_family_id", "product_variant_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductInsuranceCoverage {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "insurance_id", nullable = false)
    private Insurance insurance;

    // Either ProductFamily or ProductVariant can be covered, not both
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_family_id")
    private ProductFamily productFamily;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_variant_id")
    private ProductVariant productVariant;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CoverageStatus status;

    // Insurance's custom price for this product (optional - if null, use market price)
    @Column(name = "insurance_price", precision = 15, scale = 2)
    private BigDecimal insurancePrice;

    // Client contribution percentage for this specific product-insurance combination
    @Column(name = "client_contribution_percentage", nullable = false, precision = 5, scale = 2)
    private BigDecimal clientContributionPercentage;

    // Insurance pays this percentage (calculated as 100 - clientContributionPercentage)
    @Column(name = "insurance_coverage_percentage", nullable = false, precision = 5, scale = 2)
    private BigDecimal insuranceCoveragePercentage;

    // Whether this specific product requires pre-approval from this insurance
    @Column(nullable = false)
    private Boolean requiresPreApproval = false;

    @Enumerated(EnumType.STRING)
    private ApprovalType approvalType;

    // Maximum coverage amount for this specific product
    @Column(precision = 15, scale = 2)
    private BigDecimal maxCoverageAmount;

    // Minimum client contribution amount (fixed amount)
    @Column(precision = 15, scale = 2)
    private BigDecimal minClientContribution;

    // Maximum client contribution amount (fixed amount)
    @Column(precision = 15, scale = 2)
    private BigDecimal maxClientContribution;

    // Coverage effective dates
    @Column(nullable = false)
    private LocalDateTime effectiveFrom;

    private LocalDateTime effectiveTo;

    // Special conditions or notes for this coverage
    @Column(length = 1000)
    private String conditions;

    @Column(length = 500)
    private String approvalNotes;

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

        // Auto-calculate insurance coverage percentage
        if (clientContributionPercentage != null) {
            insuranceCoveragePercentage = BigDecimal.valueOf(100).subtract(clientContributionPercentage);
        }

        // Validation: Either productFamily or productVariant must be set, not both
        if ((productFamily == null && productVariant == null) ||
                (productFamily != null && productVariant != null)) {
            throw new IllegalStateException("Either productFamily or productVariant must be set, but not both");
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();

        // Auto-calculate insurance coverage percentage
        if (clientContributionPercentage != null) {
            insuranceCoveragePercentage = BigDecimal.valueOf(100).subtract(clientContributionPercentage);
        }
    }

    // Helper methods
    public boolean isProductFamilyCoverage() {
        return productFamily != null;
    }

    public boolean isProductVariantCoverage() {
        return productVariant != null;
    }

    public String getProductName() {
        if (productFamily != null) {
            return productFamily.getName();
        } else if (productVariant != null) {
            return productVariant.getName();
        }
        return null;
    }

    public UUID getProductId() {
        if (productFamily != null) {
            return productFamily.getId();
        } else if (productVariant != null) {
            return productVariant.getId();
        }
        return null;
    }
}