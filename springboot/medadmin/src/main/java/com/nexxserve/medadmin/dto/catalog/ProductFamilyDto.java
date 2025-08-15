package com.nexxserve.medadmin.dto.catalog;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductFamilyDto {
    private UUID id;

    @NotBlank(message = "Product family name is required")
    private String name;

    private String description;

    @NotNull(message = "Category is required")
    private CategoryDto category;


    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
    private Integer version;
    private List<ProductInsuranceCoverageResponseDto> insuranceCoverages;
}