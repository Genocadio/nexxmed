package com.nexxserve.inventoryservice.dto.stock;

import com.nexxserve.inventoryservice.dto.medicine.BrandResponseDto;
import com.nexxserve.inventoryservice.dto.medicine.GenericResponseDto;
import com.nexxserve.inventoryservice.dto.medicine.VariantResponseDto;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MedicineData {
    private String id;
    private String name;
    private String productType; // MEDICINE_GENERIC, MEDICINE_BRAND, MEDICINE_VARIANT
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Generic details
    private GenericResponseDto genericDetails;

    // Brand details
    private BrandResponseDto brandDetails;

    // Variant details
    private VariantResponseDto variantDetails;
}