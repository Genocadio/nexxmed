package com.nexxserve.inventoryservice.entity.catalog.valueobjects;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class ProductWeight {
    private BigDecimal value;
    private String unit; // kg, g, lb, oz
}