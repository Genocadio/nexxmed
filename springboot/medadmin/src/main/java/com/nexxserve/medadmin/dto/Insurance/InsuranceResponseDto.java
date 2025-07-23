package com.nexxserve.medadmin.dto.Insurance;

import com.nexxserve.medadmin.entity.Insurance;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
                .createdAt(LocalDateTime.ofInstant(insurance.getCreatedAt(), ZoneId.systemDefault()))
                .updatedAt(LocalDateTime.ofInstant(insurance.getUpdatedAt(), ZoneId.systemDefault()))
                .createdBy(insurance.getCreatedBy())
                .updatedBy(insurance.getUpdatedBy())
                .build();
    }
}