package com.nexxserve.medadmin.dto.medicine;

import com.nexxserve.medadmin.entity.medicine.TherapeuticClass;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TherapeuticClassDto {
    private UUID id;

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;

    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static TherapeuticClassDto fromEntity(TherapeuticClass entity) {
        if (entity == null) return null;

        TherapeuticClassDto dto = new TherapeuticClassDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setCreatedAt(entity.getCreatedAtAsLocalDateTime());
        dto.setUpdatedAt(entity.getUpdatedAtAsLocalDateTime());

        return dto;
    }

    public TherapeuticClass toEntity() {
        return TherapeuticClass.builder()
                .name(this.name)
                .description(this.description)
                .build();
    }
}