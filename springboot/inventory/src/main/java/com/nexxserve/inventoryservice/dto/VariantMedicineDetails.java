package com.nexxserve.inventoryservice.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class VariantMedicineDetails {
    private String form;
    private String route;
    private String tradeName;
    private String strength;
    private String concentration;
    private String packaging;
    private String notes;
    private Map<String, String> extraInfo;
    private List<String> genericIds;
    private List<GenericReference> generics;
}