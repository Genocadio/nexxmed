package com.nexxserve.catalog.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryReference {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String code;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private CategoryReference parent;

    @Column(nullable = false)
    private Integer level;

    @Column(nullable = false)
    private Integer displayOrder;

    @Column(nullable = false)
    private Boolean isActive = true;

    private String icon;
    private String description;
    private String taxCategory;
    private String regulatoryCategory;

    @ElementCollection
    @CollectionTable(name = "category_localized_names")
    @MapKeyColumn(name = "locale")
    @Column(name = "localized_name")
    private Map<String, String> localizedNames;

    @ElementCollection
    @CollectionTable(name = "category_metadata")
    @MapKeyColumn(name = "metadata_key")
    @Column(name = "metadata_value")
    private Map<String, String> metadata;
}