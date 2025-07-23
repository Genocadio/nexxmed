package com.nexxserve.inventoryservice.dto;

import com.nexxserve.inventoryservice.dto.Insurance.InsuranceCoverageDetails;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
public class ProductVariantDetails {
    private String id;
    private ProductFamilyDetails family;
    private String name;
    private String displayName;
    private String sku;
    private String upc;
    private String gtin;
    private List<String> barcodes;
    private String brand;
    private String manufacturer;
    private String manufacturerPartNumber;
    private String countryOfOrigin;
    private Map<String, String> attributes;
    private String color;
    private List<String> material;
    private String unitOfMeasure;
    private Integer unitsPerPackage;
    private List<String> allergens;
    private List<String> ingredients;
    private List<String> warnings;
    private List<String> instructions;
    private Map<String, String> specifications;
    private List<String> compatibility;
    private List<InsuranceCoverageDetails> insuranceCoverages;
    private String status;
    private Boolean isLimitedEdition;
    private String searchKeywords;
    private String seoUrl;
    private String metaDescription;
    private Double averageRating;
    private Integer reviewCount;
    private List<String> qualityCertifications;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
    private Integer version;
}
