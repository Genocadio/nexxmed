package com.nexxserve.medadmin.entity.catalog;

import com.nexxserve.medadmin.entity.BaseEntity;
import com.nexxserve.medadmin.entity.Insurance;
import com.nexxserve.medadmin.enums.ApprovalType;
import com.nexxserve.medadmin.enums.CoverageStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
public class ProductInsuranceCoverage extends BaseEntity {


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "insurance_id", nullable = false)
    private Insurance insurance;

    @Column(name = "insurance_name", nullable = false, length = 255)
    private String insuranceName;

    // Only one of ProductFamily or ProductVariant can be covered at a time
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_family_id")
    private ProductFamily productFamily;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_variant_id")
    private ProductVariant productVariant;


    // Insurance's custom price for this product (optional - if null, use market price)
    @Column(name = "insurance_price", precision = 15, scale = 2)
    private BigDecimal insurancePrice;

    // Client contribution percentage for this specific product-insurance combination
    @Column(name = "client_contribution_percentage", nullable = false, precision = 5, scale = 2)
    private BigDecimal clientContributionPercentage;


    // Whether this specific product requires pre-approval from this insurance
    @Column(nullable = false)
    @Builder.Default
    private Boolean requiresPreApproval = false;


    private String createdBy;


    private String updatedBy;


    protected void onCreate() {
        super.onCreate();
        // Auto-calculate insurance coverage percentage

        // Validation: Exactly one of productFamily or productVariant must be set
        int count = 0;
        if (productFamily != null) count++;
        if (productVariant != null) count++;

        if (count != 1) {
            throw new IllegalStateException("Exactly one of productFamily or productVariant must be set");
        }
    }

    protected void onUpdate() {
        super.onUpdate();

        // Validation: Exactly one of productFamily or productVariant must be set
        int count = 0;
        if (productFamily != null) count++;
        if (productVariant != null) count++;

        if (count != 1) {
            throw new IllegalStateException("Exactly one of productFamily or productVariant must be set");
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