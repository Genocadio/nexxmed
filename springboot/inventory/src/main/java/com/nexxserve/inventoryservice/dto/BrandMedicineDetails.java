package com.nexxserve.inventoryservice.dto;

import lombok.Data;

@Data
public class BrandMedicineDetails {
    private String brandName;
    private String manufacturer;
    private String country;
    private String variantId;
    private String variantName;
}
