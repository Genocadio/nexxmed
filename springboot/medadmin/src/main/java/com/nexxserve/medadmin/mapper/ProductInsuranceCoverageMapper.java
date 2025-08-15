package com.nexxserve.medadmin.mapper;

import com.nexxserve.medadmin.dto.catalog.ProductInsuranceCoverageRequestDto;
import com.nexxserve.medadmin.dto.catalog.ProductInsuranceCoverageResponseDto;
import com.nexxserve.medadmin.entity.Insurance;
import com.nexxserve.medadmin.entity.catalog.ProductInsuranceCoverage;
import com.nexxserve.medadmin.repository.InsuranceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
@RequiredArgsConstructor
public class ProductInsuranceCoverageMapper {

    private final InsuranceRepository insuranceRepository;

    public ProductInsuranceCoverageResponseDto toResponseDto(ProductInsuranceCoverage entity) {
        if (entity == null) return null;

        ProductInsuranceCoverageResponseDto dto = new ProductInsuranceCoverageResponseDto();
        dto.setId(entity.getId());
        dto.setInsuranceId(entity.getInsurance().getId());
        dto.setInsuranceName(entity.getInsurance().getName());

        // Product information
        if (entity.getProductFamily() != null) {
            dto.setProductFamilyId(entity.getProductFamily().getId());
            dto.setProductFamilyName(entity.getProductFamily().getName());
        }
        if (entity.getProductVariant() != null) {
            dto.setProductVariantId(entity.getProductVariant().getId());
            dto.setProductVariantName(entity.getProductVariant().getName());
        }

        // Coverage details
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

    public ProductInsuranceCoverage toEntity(ProductInsuranceCoverageRequestDto dto) {
        if (dto == null) return null;

        Insurance insurance = insuranceRepository.findById(dto.getInsuranceId())
                .orElseThrow(() -> new RuntimeException("Insurance not found with id: " + dto.getInsuranceId()));

        return ProductInsuranceCoverage.builder()
                .insurance(insurance)
                .insurancePrice(dto.getInsurancePrice())
                .clientContributionPercentage(dto.getClientContributionPercentage())
                .requiresPreApproval(dto.getRequiresPreApproval() != null ? dto.getRequiresPreApproval() : false)
                .build();
    }
}