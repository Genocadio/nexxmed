package com.nexxserve.inventoryservice.dto.medicine;

import com.nexxserve.inventoryservice.dto.Insurance.InsuranceCoverageRequestDto;
import com.nexxserve.inventoryservice.entity.medicine.Brand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class BrandRequestDto {

    @NotNull(message = "Variant ID is required")
    private UUID variantId;

    @NotBlank(message = "Brand name is required")
    @Size(max = 150, message = "Brand name must not exceed 150 characters")
    private String brandName;

    @Size(max = 150, message = "Manufacturer must not exceed 150 characters")
    private String manufacturer;

    @Size(max = 100, message = "Country must not exceed 100 characters")
    private String country;

    // Insurance coverage data
    private List<InsuranceCoverageRequestDto> insuranceCoverages;

    public Brand toEntity() {
        return Brand.builder()
                .brandName(brandName)
                .manufacturer(manufacturer)
                .country(country)
                .build();
    }
}