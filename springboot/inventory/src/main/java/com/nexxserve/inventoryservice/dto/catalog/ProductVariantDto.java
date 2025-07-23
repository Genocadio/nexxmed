package com.nexxserve.inventoryservice.dto.catalog;

import com.nexxserve.inventoryservice.entity.catalog.valueobjects.ProductWeight;
import com.nexxserve.inventoryservice.enums.ProductStatus;
import com.nexxserve.inventoryservice.enums.UnitOfMeasure;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductVariantDto {
    private UUID id;

    @NotNull(message = "Product family is required")
    private ProductFamilyDto family;

    @NotBlank(message = "Product variant name is required")
    private String name;

    @NotBlank(message = "SKU is required")
    private String sku;

    private ProductWeight weight;


    @NotBlank(message = "Brand is required")
    private String brand;

    private String countryOfOrigin;
    private String color;

    @NotNull(message = "Unit of measure is required")
    private UnitOfMeasure unitOfMeasure;


    private Map<String, String> specifications;
    // Add to ProductVariantDto
    private List<ProductInsuranceCoverageResponseDto> insuranceCoverages;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
    private Integer version;
}
