package com.nexxserve.catalog.dto;

import com.nexxserve.catalog.enums.ProductStatus;
import com.nexxserve.catalog.enums.UnitOfMeasure;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductVariantRequestDto {
    private UUID id;

    @NotNull(message = "Product family ID is required")
    private UUID familyId;

    @NotBlank(message = "Product variant name is required")
    private String name;

    private String displayName;

    @NotBlank(message = "SKU is required")
    private String sku;

    private String upc;
    private String gtin;
    private List<String> barcodes;

    @NotBlank(message = "Brand is required")
    private String brand;

    private String manufacturer;
    private String manufacturerPartNumber;
    private String countryOfOrigin;

    private Map<String, String> attributes;
    private String color;
    private List<String> material;

    // Add to ProductVariantRequestDto.java
    private List<ProductInsuranceCoverageEntry> insuranceCoverages;

    @NotNull(message = "Unit of measure is required")
    private UnitOfMeasure unitOfMeasure;

    private Integer unitsPerPackage;
    private List<String> allergens;
    private List<String> ingredients;
    private List<String> warnings;
    private List<String> instructions;
    private Map<String, String> specifications;
    private List<String> compatibility;

    @NotNull(message = "Status is required")
    private ProductStatus status;

    private Boolean isLimitedEdition;
    private String searchKeywords;
    private String seoUrl;
    private String metaDescription;
    private BigDecimal averageRating;
    private Integer reviewCount;
    private List<String> qualityCertifications;
}