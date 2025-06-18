package com.nexxserve.medicine.mapper;

import com.nexxserve.medicine.dto.InsuranceCoverageRequestDto;
import com.nexxserve.medicine.dto.InsuranceCoverageResponseDto;
import com.nexxserve.medicine.entity.Insurance;
import com.nexxserve.medicine.entity.MedicineInsuranceCoverage;
import com.nexxserve.medicine.repository.InsuranceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

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
        dto.setInsuranceCoveragePercentage(entity.getInsuranceCoveragePercentage());
        dto.setRequiresPreApproval(entity.getRequiresPreApproval());
        dto.setApprovalType(entity.getApprovalType());
        dto.setMaxCoverageAmount(entity.getMaxCoverageAmount());
        dto.setMinClientContribution(entity.getMinClientContribution());
        dto.setMaxClientContribution(entity.getMaxClientContribution());

        // Dates
        dto.setEffectiveFrom(entity.getEffectiveFrom());
        dto.setEffectiveTo(entity.getEffectiveTo());

        // Additional information
        dto.setConditions(entity.getConditions());
        dto.setApprovalNotes(entity.getApprovalNotes());

        // Audit fields
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setCreatedBy(entity.getCreatedBy());
        dto.setUpdatedBy(entity.getUpdatedBy());
        dto.setVersion(entity.getVersion());

        return dto;
    }

    public MedicineInsuranceCoverage toEntity(InsuranceCoverageRequestDto dto) {
        if (dto == null) return null;

        Insurance insurance = insuranceRepository.findById(dto.getInsuranceId())
                .orElseThrow(() -> new RuntimeException("Insurance not found with id: " + dto.getInsuranceId()));

        return MedicineInsuranceCoverage.builder()
                .insurance(insurance)
                .status(dto.getStatus())
                .insurancePrice(dto.getInsurancePrice())
                .clientContributionPercentage(dto.getClientContributionPercentage())
                .requiresPreApproval(dto.getRequiresPreApproval() != null ? dto.getRequiresPreApproval() : false)
                .approvalType(dto.getApprovalType())
                .maxCoverageAmount(dto.getMaxCoverageAmount())
                .minClientContribution(dto.getMinClientContribution())
                .maxClientContribution(dto.getMaxClientContribution())
                .effectiveFrom(dto.getEffectiveFrom())
                .effectiveTo(dto.getEffectiveTo())
                .conditions(dto.getConditions())
                .approvalNotes(dto.getApprovalNotes())
                .build();
    }
}