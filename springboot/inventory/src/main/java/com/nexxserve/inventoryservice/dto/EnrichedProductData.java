package com.nexxserve.inventoryservice.dto;

import com.nexxserve.inventoryservice.enums.SourceService;
import lombok.Data;

@Data
public class EnrichedProductData {
    private SourceService sourceService;
    private MedicineData medicineData;
    private CatalogProductData catalogProductData;
}