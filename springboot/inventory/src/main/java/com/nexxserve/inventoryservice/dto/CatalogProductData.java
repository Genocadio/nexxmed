package com.nexxserve.inventoryservice.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
public class CatalogProductData {
    private String id;
    private String name;
    private String productType; // CATALOG_PRODUCT_FAMILY, CATALOG_PRODUCT_VARIANT
    private boolean success;
    private String errorMessage;

    // Family specific fields
    private ProductFamilyDetails familyDetails;

    // Variant specific fields
    private ProductVariantDetails variantDetails;
}

