package com.nexxserve.medadmin.dto.catalog;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class ProductFamilyRequestDto {

    @NotBlank(message = "Product family name is required")
    private String name;

    private String description;

    @NotNull(message = "Category ID is required")
    private UUID categoryId;

    private String brand;


    @Valid
    private List<ProductInsuranceCoverageRequestDto> insuranceCoverages;
}