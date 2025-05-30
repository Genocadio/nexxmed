package com.nexxserve.catalog.model.valueobjects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class LocalizedContent {
    private String name;

    @Column(length = 2000)
    private String description;

    @Column(length = 500)
    private String shortDescription;

    @Column(length = 1000)
    private String searchKeywords;

    @Column(length = 500)
    private String metaDescription;

    // Store as JSON instead of @ElementCollection
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, String> specifications;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<String> warnings;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<String> instructions;
}