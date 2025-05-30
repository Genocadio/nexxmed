package com.nexxserve.catalog.model.entity;

import com.nexxserve.catalog.enums.AttributeDataType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "attribute_definitions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttributeDefinition {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String key;

    @Column(nullable = false)
    private String label;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AttributeDataType dataType;

    @ElementCollection
    @CollectionTable(name = "attribute_allowed_values")
    private List<String> allowedValues;

    private String unit;

    @Column(nullable = false)
    private Boolean isRequired = false;

    @Column(nullable = false)
    private Boolean isSearchable = false;

    @Column(nullable = false)
    private Boolean isFilterable = false;

    @Column(nullable = false)
    private Integer displayOrder;

    @ElementCollection
    @CollectionTable(name = "attribute_localized_labels")
    @MapKeyColumn(name = "locale")
    @Column(name = "localized_label")
    private Map<String, String> localizedLabels;
}