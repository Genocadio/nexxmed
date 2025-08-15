package com.nexxserve.medadmin.dto.catalog;

import com.nexxserve.medadmin.entity.catalog.valueobjects.ProductWeight;
import com.nexxserve.medadmin.enums.UnitOfMeasure;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductVariantRequestDto {
    private UUID id;

    @NotNull(message = "Product family ID is required")
    private UUID familyId;

    @NotBlank(message = "Product variant name is required")
    private String name;

    @NotBlank(message = "SKU is required")
    private String sku;

    @NotBlank(message = "Brand is required")
    private String brand;

    private String countryOfOrigin;
    private String color;
    private ProductWeight weight;

    @NotNull(message = "Unit of measure is required")
    private UnitOfMeasure unitOfMeasure;

    private Map<String, String> specifications;


    @Valid
    private List<ProductInsuranceCoverageRequestDto> insuranceCoverages;
}