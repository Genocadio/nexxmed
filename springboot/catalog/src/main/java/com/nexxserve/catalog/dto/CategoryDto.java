package com.nexxserve.catalog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryDto {
    private UUID id;

    @NotBlank(message = "Category name is required")
    private String name;

    @NotBlank(message = "Category code is required")
    private String code;

    private UUID parentId;

    @NotNull(message = "Level is required")
    private Integer level;

    @NotNull(message = "Display order is required")
    private Integer displayOrder;

    @NotNull(message = "Active status is required")
    private Boolean isActive;
    private String icon;
    private String description;
    private String taxCategory;
    private String regulatoryCategory;
    private Map<String, String> localizedNames;
    private Map<String, String> metadata;
}