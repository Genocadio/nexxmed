package com.nexxserve.catalog.model.entity;

import com.nexxserve.catalog.model.valueobjects.SustainabilityInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "packaging_info")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PackagingInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private String unitType;

    private String description;

    @ElementCollection
    @CollectionTable(name = "packaging_materials")
    private List<String> packagingMaterials;

    @Embedded
    private SustainabilityInfo sustainabilityInfo;
}