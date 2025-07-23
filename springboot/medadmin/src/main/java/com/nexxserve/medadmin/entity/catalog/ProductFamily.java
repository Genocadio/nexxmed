package com.nexxserve.medadmin.entity.catalog;

import com.nexxserve.medadmin.entity.BaseEntity;
import com.nexxserve.medadmin.enums.CoverageStatus;
import com.nexxserve.medadmin.enums.HazardClass;
import com.nexxserve.medadmin.enums.LifecycleStage;
import com.nexxserve.medadmin.enums.ProductStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "product_families")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductFamily extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private CategoryReference category;

    private String brand;

    @Column(nullable = false)
    private String createdBy;

    @Column(nullable = false)
    private String updatedBy;

    @OneToMany(mappedBy = "productFamily", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProductInsuranceCoverage> insuranceCoverages;

    // Helper method to get active insurance coverages
    public List<ProductInsuranceCoverage> getActiveInsuranceCoverages() {
        return insuranceCoverages != null ?
                insuranceCoverages.stream()
                        .toList() : List.of();
    }

}