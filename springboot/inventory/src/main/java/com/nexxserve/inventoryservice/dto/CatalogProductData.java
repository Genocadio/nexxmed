package com.nexxserve.inventoryservice.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import com.nexxserve.inventoryservice.dto.catalog.ProductFamilyDto;
import com.nexxserve.inventoryservice.dto.catalog.ProductVariantDto;

@Data
public class CatalogProductData {
    private String id;
    private String name;
    private String productType; // CATALOG_PRODUCT_FAMILY, CATALOG_PRODUCT_VARIANT
    private boolean success;
    private String errorMessage;

    private ProductFamilyDto productFamily;
    private ProductVariantDto productVariant;
}

