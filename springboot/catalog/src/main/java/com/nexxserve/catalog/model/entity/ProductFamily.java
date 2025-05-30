package com.nexxserve.catalog.model.entity;

import com.nexxserve.catalog.enums.CoverageStatus;
import com.nexxserve.catalog.enums.HazardClass;
import com.nexxserve.catalog.enums.LifecycleStage;
import com.nexxserve.catalog.enums.ProductStatus;
import com.nexxserve.catalog.model.valueobjects.LocalizedContent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "product_families")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductFamily {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(length = 500)
    private String shortDescription;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private CategoryReference category;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "product_family_subcategories")
    private List<CategoryReference> subCategories;

    @ElementCollection
    @CollectionTable(name = "product_family_tags")
    private List<String> tags;

    @Column(length = 1000)
    private String searchKeywords;

    private String brand;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductStatus status;

    private LocalDateTime launchDate;
    private LocalDateTime discontinueDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LifecycleStage lifecycleStage;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "product_family_id")
    private List<ProductImage> images;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "product_family_id")
    private List<ProductDocument> documents;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "product_family_id")
    private List<ProductVideo> videos;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "product_family_attributes")
    private List<AttributeDefinition> attributesSchema;

    @Column(nullable = false)
    private Boolean ageRestricted = false;

    @Enumerated(EnumType.STRING)
    private HazardClass hazardClass;

    @ElementCollection
    @CollectionTable(name = "product_family_certifications")
    private List<String> certifications;

    @ElementCollection
    @CollectionTable(name = "product_family_localized_content")
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

    @OneToMany(mappedBy = "productFamily", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProductInsuranceCoverage> insuranceCoverages;

    // Helper method to get active insurance coverages
    public List<ProductInsuranceCoverage> getActiveInsuranceCoverages() {
        return insuranceCoverages != null ?
                insuranceCoverages.stream()
                        .filter(coverage -> CoverageStatus.ACTIVE.equals(coverage.getStatus()))
                        .toList() : List.of();
    }


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