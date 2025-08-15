package com.nexxserve.medadmin.dto.catalog;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    private String description;
}