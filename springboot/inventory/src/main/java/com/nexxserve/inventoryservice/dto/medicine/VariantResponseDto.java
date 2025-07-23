package com.nexxserve.inventoryservice.dto.medicine;

import com.nexxserve.inventoryservice.dto.Insurance.InsuranceCoverageResponseDto;
import com.nexxserve.inventoryservice.entity.medicine.Variant;
import jakarta.validation.constraints.NotBlank;
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
public class VariantResponseDto {
    private UUID id;

    @NotBlank(message = "Name is required")
    @Size(max = 150, message = "Name must not exceed 150 characters")
    private String name;


    @Size(max = 50, message = "Form must not exceed 50 characters")
    private String form;

    @Size(max = 50, message = "Route must not exceed 50 characters")
    private String route;

    @Size(max = 150, message = "Trade name must not exceed 150 characters")
    private String tradeName;

    @Size(max = 50, message = "Strength must not exceed 50 characters")
    private String strength;

    @Size(max = 50, message = "Concentration must not exceed 50 characters")
    private String concentration;

    @Size(max = 100, message = "Packaging must not exceed 100 characters")
    private String packaging;

    private String notes;
    private List<GenericResponseDto> generics;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<InsuranceCoverageResponseDto> insuranceCoverages;

    public static VariantResponseDto fromEntity(Variant variant) {
        if (variant == null) return null;

        return VariantResponseDto.builder()
                .id(variant.getId())
                .name(variant.getName())
                .form(variant.getForm())
                .route(variant.getRoute())
                .tradeName(variant.getTradeName())
                .strength(variant.getStrength())
                .concentration(variant.getConcentration())
                .packaging(variant.getPackaging())
                .notes(variant.getNotes())
                .generics(variant.getGenerics() != null ?
                        variant.getGenerics().stream()
                                .map(GenericResponseDto::toDto)
                                .toList() : null)
                .createdAt(variant.getCreatedAtAsLocalDateTime())
                .updatedAt(variant.getUpdatedAtAsLocalDateTime())
                .insuranceCoverages(variant.getInsuranceCoverages() != null ?
                        variant.getInsuranceCoverages().stream()
                                .map(InsuranceCoverageResponseDto::toResponseDto)
                                .toList() : null)
                .build();
    }


}