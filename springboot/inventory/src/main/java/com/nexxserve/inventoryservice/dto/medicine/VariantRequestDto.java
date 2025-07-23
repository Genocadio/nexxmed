package com.nexxserve.inventoryservice.dto.medicine;

import com.nexxserve.inventoryservice.dto.Insurance.InsuranceCoverageRequestDto;
import com.nexxserve.inventoryservice.entity.medicine.Variant;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VariantRequestDto {

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
    private List<InsuranceCoverageRequestDto> insuranceCoverages;


    public Variant toEntity() {
        return Variant.builder()
                .name(name)
                .form(form)
                .route(route)
                .tradeName(tradeName)
                .strength(strength)
                .concentration(concentration)
                .packaging(packaging)
                .notes(notes)
                .build();
    }
}