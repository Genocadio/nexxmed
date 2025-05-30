package com.nexxserve.medicine.entity;

import com.nexxserve.medicine.enums.CoverageStatus;
import com.nexxserve.medicine.enums.ApprovalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "medicine_insurance_coverages",
        uniqueConstraints = @UniqueConstraint(columnNames = {"insurance_id", "generic_id", "brand_id", "variant_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicineInsuranceCoverage {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "insurance_id", nullable = false)
    private UUID insuranceId;

    @Column(name = "insurance_name", nullable = false, length = 255)
    private String insuranceName;

    // Only one of Generic, Brand, or Variant can be covered at a time
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "generic_id")
    private Generic generic;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id")
    private Brand brand;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id")
    private Variant variant;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CoverageStatus status;

    // Insurance's custom price for this medicine (optional - if null, use market price)
    @Column(name = "insurance_price", precision = 15, scale = 2)
    private BigDecimal insurancePrice;

    // Client contribution percentage for this specific medicine-insurance combination
    @Column(name = "client_contribution_percentage", nullable = false, precision = 5, scale = 2)
    private BigDecimal clientContributionPercentage;

    // Insurance pays this percentage (calculated as 100 - clientContributionPercentage)
    @Column(name = "insurance_coverage_percentage", nullable = false, precision = 5, scale = 2)
    private BigDecimal insuranceCoveragePercentage;

    // Whether this specific medicine requires pre-approval from this insurance
    @Column(nullable = false)
    private Boolean requiresPreApproval = false;

    @Enumerated(EnumType.STRING)
    private ApprovalType approvalType;

    // Maximum coverage amount for this specific medicine
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

        // Validation: Exactly one of generic, brand, or variant must be set
        int count = 0;
        if (generic != null) count++;
        if (brand != null) count++;
        if (variant != null) count++;

        if (count != 1) {
            throw new IllegalStateException("Exactly one of generic, brand, or variant must be set");
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();

        // Auto-calculate insurance coverage percentage
        if (clientContributionPercentage != null) {
            insuranceCoveragePercentage = BigDecimal.valueOf(100).subtract(clientContributionPercentage);
        }

        // Validation: Exactly one of generic, brand, or variant must be set
        int count = 0;
        if (generic != null) count++;
        if (brand != null) count++;
        if (variant != null) count++;

        if (count != 1) {
            throw new IllegalStateException("Exactly one of generic, brand, or variant must be set");
        }
    }

    // Helper methods
    public boolean isGenericCoverage() {
        return generic != null;
    }

    public boolean isBrandCoverage() {
        return brand != null;
    }

    public boolean isVariantCoverage() {
        return variant != null;
    }

    public String getMedicineName() {
        if (generic != null) {
            return generic.getName();
        } else if (brand != null) {
            return brand.getBrandName();
        } else if (variant != null) {
            return variant.getName();
        }
        return null;
    }

    public UUID getMedicineId() {
        if (generic != null) {
            return generic.getId();
        } else if (brand != null) {
            return brand.getId();
        } else if (variant != null) {
            return variant.getId();
        }
        return null;
    }
}