package com.nexxserve.medicine.dto;

import com.nexxserve.medicine.entity.Insurance;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InsuranceResponseDto {
    private UUID id;
    private String name;
    private String abbreviation;
    private BigDecimal defaultClientContributionPercentage;
    private Boolean defaultRequiresPreApproval;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;

    public static InsuranceResponseDto fromEntity(Insurance insurance) {
        return InsuranceResponseDto.builder()
                .id(insurance.getId())
                .name(insurance.getName())
                .abbreviation(insurance.getAbbreviation())
                .defaultClientContributionPercentage(insurance.getDefaultClientContributionPercentage())
                .defaultRequiresPreApproval(insurance.getDefaultRequiresPreApproval())
                .active(insurance.getActive())
                .createdAt(insurance.getCreatedAt())
                .updatedAt(insurance.getUpdatedAt())
                .createdBy(insurance.getCreatedBy())
                .updatedBy(insurance.getUpdatedBy())
                .build();
    }
}