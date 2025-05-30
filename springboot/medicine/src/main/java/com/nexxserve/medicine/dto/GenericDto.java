package com.nexxserve.medicine.dto;

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
}