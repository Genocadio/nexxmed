package com.nexxserve.catalog.model.valueobjects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.Embeddable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class WarrantyInfo {
    private Integer durationMonths;
    private String terms;
    private String coverage;
    private String provider;
}
