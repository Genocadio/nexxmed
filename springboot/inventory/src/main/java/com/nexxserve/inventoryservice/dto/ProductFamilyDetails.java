package com.nexxserve.inventoryservice.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ProductFamilyDetails {
    private String id;
    private String name;
    private String description;
    private String shortDescription;
    private CategoryDetails category;
    private List<String> subCategoryIds;
    private List<String> tags;
    private String searchKeywords;
    private String brand;
    private String status;
    private LocalDateTime launchDate;
    private LocalDateTime discontinueDate;
    private String lifecycleStage;
    private Boolean ageRestricted;
    private String hazardClass;
    private List<String> certifications;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
    private Integer version;
    private List<InsuranceCoverageDetails> insuranceCoverages;
}
