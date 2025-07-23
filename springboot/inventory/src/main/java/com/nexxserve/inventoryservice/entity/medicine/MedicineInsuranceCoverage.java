package com.nexxserve.inventoryservice.entity.medicine;

import com.nexxserve.inventoryservice.entity.BaseEntity;
import com.nexxserve.inventoryservice.entity.Insurance;
import com.nexxserve.inventoryservice.enums.ApprovalType;
import com.nexxserve.inventoryservice.enums.CoverageStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
public class MedicineInsuranceCoverage extends BaseEntity {


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "insurance_id", nullable = false)
    private Insurance insurance;

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


    // Whether this specific medicine requires pre-approval from this insurance
    @Column(nullable = false)
    @Builder.Default
    private Boolean requiresPreApproval = false;



    @Column(nullable = false)
    private String createdBy;

    @Column(nullable = false)
    private String updatedBy;


    @Override
    protected void onCreate() {
        super.onCreate();



        // Validation: Exactly one of generic, brand, or variant must be set
        int count = 0;
        if (generic != null) count++;
        if (brand != null) count++;
        if (variant != null) count++;

        if (count != 1) {
            throw new IllegalStateException("Exactly one of generic, brand, or variant must be set");
        }
    }

    @Override
    protected void onUpdate() {
        super.onUpdate();



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