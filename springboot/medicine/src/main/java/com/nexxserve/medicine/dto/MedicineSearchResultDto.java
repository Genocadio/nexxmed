package com.nexxserve.medicine.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicineSearchResultDto {
    private UUID id;
    private String name;
    private String description;
    private String type; // "BRAND", "GENERIC", "VARIANT"
    private String form;
    private String route;
    private String strength;
    private String manufacturer;
    private String additionalInfo; // For any type-specific information
    private UUID parentId; // For hierarchical relationships
}