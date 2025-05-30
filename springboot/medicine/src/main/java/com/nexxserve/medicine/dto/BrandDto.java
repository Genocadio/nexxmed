package com.nexxserve.medicine.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class BrandDto {
    private UUID id;

    @NotNull(message = "Variant ID is required")
    private UUID variantId;

    private String variantName;

    @NotBlank(message = "Brand name is required")
    @Size(max = 150, message = "Brand name must not exceed 150 characters")
    private String brandName;

    @Size(max = 150, message = "Manufacturer must not exceed 150 characters")
    private String manufacturer;

    @Size(max = 100, message = "Country must not exceed 100 characters")
    private String country;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<InsuranceCoverageResponseDto> insuranceCoverages;
}