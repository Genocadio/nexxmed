package com.nexxserve.inventoryservice.entity.catalog;

import com.nexxserve.inventoryservice.entity.BaseEntity;
import com.nexxserve.inventoryservice.entity.catalog.valueobjects.*;
import com.nexxserve.inventoryservice.enums.CoverageStatus;
import com.nexxserve.inventoryservice.enums.ProductStatus;
import com.nexxserve.inventoryservice.enums.UnitOfMeasure;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Entity
@Table(name = "product_variants")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductVariant extends BaseEntity {


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "family_id", nullable = false)
    private ProductFamily family;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String sku;


    @Column(nullable = false)
    private String brand;

    private String country;


    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "unit", column = @Column(name = "dimension_unit"))
    })
    private ProductDimensions dimensions;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "unit", column = @Column(name = "weight_unit"))
    })
    private ProductWeight weight;

    private String color;


    @Enumerated(EnumType.STRING)
    @Column(name = "unit_of_measure", nullable = false)
    private UnitOfMeasure unitOfMeasure;


    @ElementCollection
    @CollectionTable(name = "product_variant_specifications")
    @MapKeyColumn(name = "spec_key")
    @Column(name = "spec_value")
    private Map<String, String> specifications;


    @Column(nullable = false)
    private String createdBy;

    @Column(nullable = false)
    private String updatedBy;



    @OneToMany(mappedBy = "productVariant", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProductInsuranceCoverage> insuranceCoverages;

    // Helper method to get active insurance coverages
    public List<ProductInsuranceCoverage> getActiveInsuranceCoverages() {
        return insuranceCoverages != null ?
                insuranceCoverages.stream()
                        .toList() : List.of();
    }

    // Helper method to check if product has insurance coverage
    public boolean hasInsuranceCoverage() {
        return !getActiveInsuranceCoverages().isEmpty();
    }

    // Helper method to get coverage for specific insurance
    public Optional<ProductInsuranceCoverage> getCoverageForInsurance(UUID insuranceId) {
        return getActiveInsuranceCoverages().stream()
                .filter(coverage -> coverage.getInsurance().getId().equals(insuranceId))
                .findFirst();
    }
}
