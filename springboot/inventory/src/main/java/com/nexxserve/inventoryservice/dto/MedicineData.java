package com.nexxserve.inventoryservice.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
public class MedicineData {
    private String id;
    private String name;
    private String productType; // MEDICINE_GENERIC, MEDICINE_BRAND, MEDICINE_VARIANT
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Generic details
    private GenericMedicineDetails genericDetails;

    // Brand details
    private BrandMedicineDetails brandDetails;

    // Variant details
    private VariantMedicineDetails variantDetails;

    // Insurance coverages
    private List<MedicineInsuranceCoverage> insuranceCoverages;
}

