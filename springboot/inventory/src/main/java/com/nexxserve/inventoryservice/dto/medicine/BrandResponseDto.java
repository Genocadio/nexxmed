package com.nexxserve.inventoryservice.dto.medicine;

import com.nexxserve.inventoryservice.dto.Insurance.InsuranceCoverageResponseDto;
import com.nexxserve.inventoryservice.entity.medicine.Brand;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BrandResponseDto {
    private UUID id;
    private VariantResponseDto variant;
    private String brandName;
    private String manufacturer;
    private String country;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<InsuranceCoverageResponseDto> insuranceCoverages;

    public static BrandResponseDto toDto(Brand entity){
        if (entity == null) return null;
        return BrandResponseDto.builder()
                .id(entity.getId())
                .variant(entity.getVariant() != null ? VariantResponseDto.fromEntity(entity.getVariant()) : null)
                .brandName(entity.getBrandName())
                .manufacturer(entity.getManufacturer())
                .country(entity.getCountry())
                .createdAt(entity.getCreatedAtAsLocalDateTime())
                .updatedAt(entity.getUpdatedAtAsLocalDateTime())
                .insuranceCoverages(entity.getInsuranceCoverages() != null ? entity.getInsuranceCoverages().stream()
                        .map(InsuranceCoverageResponseDto::toResponseDto).toList() : null)
                .build();
    }
}
