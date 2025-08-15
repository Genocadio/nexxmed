package com.nexxserve.medadmin.dto.medicine;

import com.nexxserve.medadmin.dto.Insurance.InsuranceCoverageResponseDto;
import com.nexxserve.medadmin.entity.medicine.Generic;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GenericResponseDto {

    private UUID id;

    @NotBlank(message = "Name is required")
    @Size(max = 150, message = "Name must not exceed 150 characters")
    private String name;

    @Size(max = 150, message = "Chemical name must not exceed 150 characters")
    private String chemicalName;
    private TherapeuticClassDto therapeuticClass;
    private String description;
    private Boolean isParent;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<InsuranceCoverageResponseDto> insuranceCoverages;

    public static GenericResponseDto toDto(Generic entity) {
        if (entity == null) return null;
        return GenericResponseDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .chemicalName(entity.getChemicalName())
                .therapeuticClass(TherapeuticClassDto.fromEntity(entity.getTherapeuticClass()))
                .description(entity.getDescription())
                .insuranceCoverages(entity.getInsuranceCoverages() != null ?
                    entity.getInsuranceCoverages().stream()
                        .map(InsuranceCoverageResponseDto::toResponseDto)
                        .collect(Collectors.toList()) : null)
                .isParent(entity.getIsParent())
                .createdAt(entity.getCreatedAtAsLocalDateTime())
                .updatedAt(entity.getUpdatedAtAsLocalDateTime())
                .build();

    }
}
