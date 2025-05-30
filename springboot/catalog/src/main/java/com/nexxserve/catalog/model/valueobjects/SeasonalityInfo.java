package com.nexxserve.catalog.model.valueobjects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Column;
import java.time.Month;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class SeasonalityInfo {

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Set<Month> availableMonths;

    private String description;
}