package com.nexxserve.catalog.model.entity;

import com.nexxserve.catalog.enums.CoverageStatus;
import com.nexxserve.catalog.enums.ProductStatus;
import com.nexxserve.catalog.enums.UnitOfMeasure;
import com.nexxserve.catalog.model.valueobjects.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
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
public class ProductVariant {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "family_id", nullable = false)
    private ProductFamily family;

    @Column(nullable = false)
    private String name;

    private String displayName;

    @Column(unique = true, nullable = false)
    private String sku;

    private String upc;
    private String gtin;

    @ElementCollection
    @CollectionTable(name = "product_variant_barcodes")
    private List<String> barcodes;

    @Column(nullable = false)
    private String brand;

    private String manufacturer;
    private String manufacturerPartNumber;
    private String countryOfOrigin;

    @ElementCollection
    @CollectionTable(name = "product_variant_attributes")
    @MapKeyColumn(name = "attribute_key")
    @Column(name = "attribute_value")
    private Map<String, String> attributes;

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

    @ElementCollection
    @CollectionTable(name = "product_variant_materials")
    private List<String> material;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "packaging_id")
    private PackagingInfo packaging;

    @Enumerated(EnumType.STRING)
    @Column(name = "unit_of_measure", nullable = false)
    private UnitOfMeasure unitOfMeasure;

    private Integer unitsPerPackage;

    @ElementCollection
    @CollectionTable(name = "product_variant_allergens")
    private List<String> allergens;

    @Embedded
    private NutritionalInfo nutritionalInfo;

    @ElementCollection
    @CollectionTable(name = "product_variant_ingredients")
    private List<String> ingredients;

    @ElementCollection
    @CollectionTable(name = "product_variant_warnings")
    private List<String> warnings;

    @ElementCollection
    @CollectionTable(name = "product_variant_instructions")
    private List<String> instructions;

    @ElementCollection
    @CollectionTable(name = "product_variant_specifications")
    @MapKeyColumn(name = "spec_key")
    @Column(name = "spec_value")
    private Map<String, String> specifications;

    @ElementCollection
    @CollectionTable(name = "product_variant_compatibility")
    private List<String> compatibility;

    @Embedded
    private WarrantyInfo warranty;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "product_variant_id")
    private List<ProductImage> images;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "product_variant_id")
    private List<ProductDocument> documents;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "product_variant_id")
    private List<ProductVideo> videos;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductStatus status;

    @Embedded
    private SeasonalityInfo seasonality;

    @Column(nullable = false)
    private Boolean isLimitedEdition = false;

    @Column(length = 1000)
    private String searchKeywords;

    private String seoUrl;

    @Column(length = 500)
    private String metaDescription;

    private BigDecimal averageRating;
    private Integer reviewCount;

    @ElementCollection
    @CollectionTable(name = "product_variant_quality_certifications")
    private List<String> qualityCertifications;

    @ElementCollection
    @CollectionTable(name = "product_variant_localized_content")
    @MapKeyColumn(name = "locale")
    private Map<String, LocalizedContent> localizedContent;

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

    @OneToMany(mappedBy = "productVariant", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProductInsuranceCoverage> insuranceCoverages;

    // Helper method to get active insurance coverages
    public List<ProductInsuranceCoverage> getActiveInsuranceCoverages() {
        return insuranceCoverages != null ?
                insuranceCoverages.stream()
                        .filter(coverage -> CoverageStatus.ACTIVE.equals(coverage.getStatus()))
                        .toList() : List.of();
    }

    // Helper method to check if product has insurance coverage
    public boolean hasInsuranceCoverage() {
        return getActiveInsuranceCoverages().size() > 0;
    }

    // Helper method to get coverage for specific insurance
    public Optional<ProductInsuranceCoverage> getCoverageForInsurance(UUID insuranceId) {
        return getActiveInsuranceCoverages().stream()
                .filter(coverage -> coverage.getInsurance().getId().equals(insuranceId))
                .findFirst();
    }
}
