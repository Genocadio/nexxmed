package com.nexxserve.inventoryservice.mapper;

import com.nexxserve.inventoryservice.dto.Insurance.InsuranceCoverageRequestDto;
import com.nexxserve.inventoryservice.dto.Insurance.InsuranceCoverageResponseDto;
import com.nexxserve.inventoryservice.entity.Insurance;
import com.nexxserve.inventoryservice.entity.medicine.MedicineInsuranceCoverage;
import com.nexxserve.inventoryservice.repository.InsuranceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
@RequiredArgsConstructor
public class InsuranceCoverageMapper {

    private final InsuranceRepository insuranceRepository;

    public InsuranceCoverageResponseDto toResponseDto(MedicineInsuranceCoverage entity) {
        if (entity == null) return null;

        InsuranceCoverageResponseDto dto = new InsuranceCoverageResponseDto();
        dto.setId(entity.getId());
        dto.setInsuranceId(entity.getInsurance().getId());
        dto.setInsuranceName(entity.getInsuranceName());


        // Coverage details
        dto.setStatus(entity.getStatus());
        dto.setInsurancePrice(entity.getInsurancePrice());
        dto.setClientContributionPercentage(entity.getClientContributionPercentage());
        dto.setRequiresPreApproval(entity.getRequiresPreApproval());
        // Audit fields
        dto.setCreatedAt(LocalDateTime.ofInstant(entity.getCreatedAt(), ZoneId.systemDefault()));
        dto.setUpdatedAt(LocalDateTime.ofInstant(entity.getUpdatedAt(), ZoneId.systemDefault()));
        dto.setCreatedBy(entity.getCreatedBy());
        dto.setUpdatedBy(entity.getUpdatedBy());

        return dto;
    }

    public MedicineInsuranceCoverage toEntity(InsuranceCoverageRequestDto dto) {
        if (dto == null) return null;

        Insurance insurance = insuranceRepository.findById(dto.getInsuranceId())
                .orElseThrow(() -> new RuntimeException("Insurance not found with id: " + dto.getInsuranceId()));

        return MedicineInsuranceCoverage.builder()
                .insurance(insurance)
                .insuranceName(insurance.getName())
                .status(dto.getStatus())
                .insurancePrice(dto.getInsurancePrice())
                .clientContributionPercentage(dto.getClientContributionPercentage())
                .requiresPreApproval(dto.getRequiresPreApproval() != null ? dto.getRequiresPreApproval() : false)
                .build();
    }
}