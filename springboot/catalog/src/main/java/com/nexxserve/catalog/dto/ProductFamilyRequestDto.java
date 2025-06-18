package com.nexxserve.catalog.dto;

import com.nexxserve.catalog.enums.HazardClass;
import com.nexxserve.catalog.enums.LifecycleStage;
import com.nexxserve.catalog.enums.ProductStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductFamilyRequestDto {
    private UUID id;

    @NotBlank(message = "Product family name is required")
    private String name;

    private String description;
    private String shortDescription;

    @NotNull(message = "Category ID is required")
    private UUID categoryId;

    private List<UUID> subCategoryIds;
    private List<String> tags;
    private String searchKeywords;
    private String brand;

    @NotNull(message = "Status is required")
    private ProductStatus status;

    private LocalDateTime launchDate;
    private LocalDateTime discontinueDate;

    @NotNull(message = "Lifecycle stage is required")
    private LifecycleStage lifecycleStage;

    @NotNull(message = "Age restriction status is required")
    private Boolean ageRestricted;
    private HazardClass hazardClass;
    private List<String> certifications;
    private Integer version;
}