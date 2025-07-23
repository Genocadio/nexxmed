package com.nexxserve.inventoryservice.service;

import com.nexxserve.inventoryservice.dto.Insurance.InsuranceCoverageRequestDto;
import com.nexxserve.inventoryservice.dto.Insurance.InsuranceCoverageResponseDto;
import com.nexxserve.inventoryservice.entity.medicine.Brand;
import com.nexxserve.inventoryservice.entity.medicine.Generic;
import com.nexxserve.inventoryservice.entity.medicine.MedicineInsuranceCoverage;
import com.nexxserve.inventoryservice.entity.medicine.Variant;
import com.nexxserve.inventoryservice.mapper.InsuranceCoverageMapper;
import com.nexxserve.inventoryservice.repository.medicine.BrandRepository;
import com.nexxserve.inventoryservice.repository.medicine.GenericRepository;
import com.nexxserve.inventoryservice.repository.medicine.MedicineInsuranceCoverageRepository;
import com.nexxserve.inventoryservice.repository.medicine.VariantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class InsuranceCoverageService {

    private final MedicineInsuranceCoverageRepository repository;
    private final GenericRepository genericRepository;
    private final BrandRepository brandRepository;
    private final VariantRepository variantRepository;
    private final InsuranceCoverageMapper mapper;

    public List<InsuranceCoverageResponseDto> findAll() {
        return repository.findAll().stream()
                .map(mapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public InsuranceCoverageResponseDto findById(UUID id) {
        MedicineInsuranceCoverage coverage = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Insurance coverage not found with id: " + id));
        return mapper.toResponseDto(coverage);
    }

    public List<InsuranceCoverageResponseDto> findByInsuranceId(UUID insuranceId) {
        return repository.findByInsuranceId(insuranceId).stream()
                .map(mapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public List<InsuranceCoverageResponseDto> findByGenericId(UUID genericId) {
        return repository.findByGenericId(genericId).stream()
                .map(mapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public List<InsuranceCoverageResponseDto> findByBrandId(UUID brandId) {
        return repository.findByBrandId(brandId).stream()
                .map(mapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public List<InsuranceCoverageResponseDto> findByVariantId(UUID variantId) {
        return repository.findByVariantId(variantId).stream()
                .map(mapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public InsuranceCoverageResponseDto create(InsuranceCoverageRequestDto requestDto, String username) {
        validateRequest(requestDto);

        MedicineInsuranceCoverage entity = mapper.toEntity(requestDto);


        // Set created/updated by
        entity.setCreatedBy(username);
        entity.setUpdatedBy(username);


        // Set appropriate medicine entity based on provided IDs
        if (requestDto.getGenericId() != null) {
            Generic generic = genericRepository.findById(requestDto.getGenericId())
                    .orElseThrow(() -> new RuntimeException("Generic not found with id: " + requestDto.getGenericId()));
            entity.setGeneric(generic);
        } else if (requestDto.getBrandId() != null) {
            Brand brand = brandRepository.findById(requestDto.getBrandId())
                    .orElseThrow(() -> new RuntimeException("Brand not found with id: " + requestDto.getBrandId()));
            entity.setBrand(brand);
        } else if (requestDto.getVariantId() != null) {
            Variant variant = variantRepository.findById(requestDto.getVariantId())
                    .orElseThrow(() -> new RuntimeException("Variant not found with id: " + requestDto.getVariantId()));
            entity.setVariant(variant);
        }

        MedicineInsuranceCoverage saved = repository.save(entity);
        return mapper.toResponseDto(saved);
    }

   public InsuranceCoverageResponseDto update(UUID id, InsuranceCoverageRequestDto requestDto, String username) {
       validateRequest(requestDto);

       MedicineInsuranceCoverage existing = repository.findById(id)
               .orElseThrow(() -> new RuntimeException("Insurance coverage not found with id: " + id));

       // Update fields
       // Insurance relationship is handled by the mapper when creating a new entity
       // We need to replace the insurance entity instead of trying to set an ID
       existing.setInsurance(mapper.toEntity(requestDto).getInsurance());
       existing.setInsuranceName(mapper.toEntity(requestDto).getInsuranceName());

       existing.setStatus(requestDto.getStatus());
       existing.setInsurancePrice(requestDto.getInsurancePrice());
       existing.setClientContributionPercentage(requestDto.getClientContributionPercentage());
       existing.setRequiresPreApproval(requestDto.getRequiresPreApproval() != null ?
               requestDto.getRequiresPreApproval() : false);
       existing.setUpdatedBy(username);


       // Check if medicine type has changed
       boolean medicineTypeChanged = false;

       if (requestDto.getGenericId() != null && (existing.getGeneric() == null ||
               !existing.getGeneric().getId().equals(requestDto.getGenericId()))) {
           medicineTypeChanged = true;
       } else if (requestDto.getBrandId() != null && (existing.getBrand() == null ||
               !existing.getBrand().getId().equals(requestDto.getBrandId()))) {
           medicineTypeChanged = true;
       } else if (requestDto.getVariantId() != null && (existing.getVariant() == null ||
               !existing.getVariant().getId().equals(requestDto.getVariantId()))) {
           medicineTypeChanged = true;
       }

       // If medicine type changed, update the entity relationships
       if (medicineTypeChanged) {
           // Clear existing relationships
           existing.setGeneric(null);
           existing.setBrand(null);
           existing.setVariant(null);

           // Set the new relationship
           if (requestDto.getGenericId() != null) {
               Generic generic = genericRepository.findById(requestDto.getGenericId())
                       .orElseThrow(() -> new RuntimeException("Generic not found with id: " + requestDto.getGenericId()));
               existing.setGeneric(generic);
           } else if (requestDto.getBrandId() != null) {
               Brand brand = brandRepository.findById(requestDto.getBrandId())
                       .orElseThrow(() -> new RuntimeException("Brand not found with id: " + requestDto.getBrandId()));
               existing.setBrand(brand);
           } else if (requestDto.getVariantId() != null) {
               Variant variant = variantRepository.findById(requestDto.getVariantId())
                       .orElseThrow(() -> new RuntimeException("Variant not found with id: " + requestDto.getVariantId()));
               existing.setVariant(variant);
           }
       }

       MedicineInsuranceCoverage updated = repository.save(existing);
       return mapper.toResponseDto(updated);
   }

    public void delete(UUID id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Insurance coverage not found with id: " + id);
        }
        repository.deleteById(id);
    }

    private void validateRequest(InsuranceCoverageRequestDto dto) {
        // Count how many medicine types are provided
        int count = 0;
        if (dto.getGenericId() != null) count++;
        if (dto.getBrandId() != null) count++;
        if (dto.getVariantId() != null) count++;

        if (count != 1) {
            throw new IllegalArgumentException("Exactly one of genericId, brandId, or variantId must be provided");
        }

        // Check for duplicate coverage
        if (dto.getGenericId() != null) {
            repository.findByInsuranceIdAndGenericId(dto.getInsuranceId(), dto.getGenericId())
                .ifPresent(coverage -> {
                    throw new IllegalStateException("Coverage already exists for this insurance and generic");
                });
        } else if (dto.getBrandId() != null) {
            repository.findByInsuranceIdAndBrandId(dto.getInsuranceId(), dto.getBrandId())
                .ifPresent(coverage -> {
                    throw new IllegalStateException("Coverage already exists for this insurance and brand");
                });
        } else if (dto.getVariantId() != null) {
            repository.findByInsuranceIdAndVariantId(dto.getInsuranceId(), dto.getVariantId())
                .ifPresent(coverage -> {
                    throw new IllegalStateException("Coverage already exists for this insurance and variant");
                });
        }
    }
}