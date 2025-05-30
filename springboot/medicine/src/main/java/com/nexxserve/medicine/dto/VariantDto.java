package com.nexxserve.medicine.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
public class VariantDto {
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
    private Map<String, Object> extraInfo;
    private List<UUID> genericIds;
    private List<GenericDto> generics;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<InsuranceCoverageResponseDto> insuranceCoverages;
}