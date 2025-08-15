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
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GenericDto {
    private UUID id;

    @NotBlank(message = "Name is required")
    @Size(max = 150, message = "Name must not exceed 150 characters")
    private String name;

    @Size(max = 150, message = "Chemical name must not exceed 150 characters")
    private String chemicalName;

    private UUID classId;
    private String className;
    private String description;
    private Boolean isParent;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<InsuranceCoverageResponseDto> insuranceCoverages;

    public GenericDto toDto(Generic entity) {
        if (entity == null) return null;
        return GenericDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .chemicalName(entity.getChemicalName())
                .classId(entity.getTherapeuticClass() != null ? entity.getTherapeuticClass().getId() : null)
                .className(entity.getTherapeuticClass() != null ? entity.getTherapeuticClass().getName() : null)
                .description(entity.getDescription())
                .isParent(entity.getIsParent())
                .createdAt(entity.getCreatedAt() != null ? LocalDateTime.ofInstant(entity.getCreatedAt(), ZoneId.systemDefault()) : null)
                .updatedAt(entity.getUpdatedAt() != null ? LocalDateTime.ofInstant(entity.getUpdatedAt(), ZoneId.systemDefault()) : null)
                .build();
    }
}