package com.nexxserve.medadmin.entity.catalog.valueobjects;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class ProductDimensions {
    private BigDecimal length;
    private BigDecimal width;
    private BigDecimal height;
    private String unit; // cm, inch, etc.
}