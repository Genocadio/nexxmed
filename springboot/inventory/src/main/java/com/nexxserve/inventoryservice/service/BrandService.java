package com.nexxserve.inventoryservice.service;

import com.nexxserve.inventoryservice.dto.medicine.BrandDto;
import com.nexxserve.inventoryservice.dto.medicine.BrandRequestDto;
import com.nexxserve.inventoryservice.dto.medicine.BrandResponseDto;
import com.nexxserve.inventoryservice.entity.medicine.Brand;
import com.nexxserve.inventoryservice.entity.medicine.MedicineInsuranceCoverage;
import com.nexxserve.inventoryservice.entity.medicine.Variant;
import com.nexxserve.inventoryservice.mapper.InsuranceCoverageMapper;
import com.nexxserve.inventoryservice.repository.medicine.BrandRepository;
import com.nexxserve.inventoryservice.repository.medicine.MedicineInsuranceCoverageRepository;
import com.nexxserve.inventoryservice.repository.medicine.VariantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class BrandService {

    private final BrandRepository repository;
    private final VariantRepository variantRepository;
    private final InsuranceCoverageMapper insuranceCoverageMapper;
    private final MedicineInsuranceCoverageRepository insuranceCoverageRepository;


    public BrandResponseDto create(BrandRequestDto requestDto) {
        Brand entity = requestDto.toEntity();

        Variant variant = variantRepository.findById(requestDto.getVariantId())
                .orElseThrow(() -> new RuntimeException("Variant not found with id: " + requestDto.getVariantId()));
        entity.setVariant(variant);

        Brand saved = repository.save(entity);

        // Handle insurance coverages if present
        if (requestDto.getInsuranceCoverages() != null && !requestDto.getInsuranceCoverages().isEmpty()) {
            List<MedicineInsuranceCoverage> coverages = new ArrayList<>();

            for (var coverageDto : requestDto.getInsuranceCoverages()) {
                MedicineInsuranceCoverage coverage = insuranceCoverageMapper.toEntity(coverageDto);
                coverage.setBrand(saved);
                coverages.add(coverage);
            }

            insuranceCoverageRepository.saveAll(coverages);
        }

        return findById(saved.getId());
    }

    public BrandResponseDto update(UUID id, BrandRequestDto requestDto) {
        Brand existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Brand not found with id: " + id));

        existing.setBrandName(requestDto.getBrandName());
        existing.setManufacturer(requestDto.getManufacturer());
        existing.setCountry(requestDto.getCountry());

        if (!existing.getVariant().getId().equals(requestDto.getVariantId())) {
            Variant variant = variantRepository.findById(requestDto.getVariantId())
                    .orElseThrow(() -> new RuntimeException("Variant not found with id: " + requestDto.getVariantId()));
            existing.setVariant(variant);
        }

        Brand updated = repository.save(existing);

        // Handle insurance coverages if present
        if (requestDto.getInsuranceCoverages() != null && !requestDto.getInsuranceCoverages().isEmpty()) {
            insuranceCoverageRepository.deleteByBrandId(updated.getId());
            List<MedicineInsuranceCoverage> coverages = new ArrayList<>();

            for (var coverageDto : requestDto.getInsuranceCoverages()) {
                MedicineInsuranceCoverage coverage = insuranceCoverageMapper.toEntity(coverageDto);
                coverage.setBrand(updated);
                coverage.setCreatedBy("system");
                coverage.setUpdatedBy("system");
                coverages.add(coverage);
                if(coverage.getInsuranceName() == null || coverage.getInsuranceName().isEmpty()) {
                    throw new RuntimeException("Insurance name cannot be empty");
                }
            }


            insuranceCoverageRepository.saveAll(coverages);
        } else {
            // If no insurance coverages are provided, delete existing ones
            insuranceCoverageRepository.deleteByBrandId(updated.getId());
        }

        return findById(updated.getId());
    }


    public List<BrandResponseDto> findAll() {
        return repository.findAll().stream()
                .map(BrandResponseDto::toDto)
                .toList();
    }

    public BrandResponseDto findById(UUID id) {
        return repository.findById(id)
                .map(BrandResponseDto::toDto)
                .orElseThrow(() -> new RuntimeException("Brand not found with id: " + id));
    }

    public List<BrandResponseDto> searchByBrandName(String brandName) {
        return repository.findByBrandNameContainingIgnoreCase(brandName).stream()
                .map(BrandResponseDto::toDto)
                .toList();
    }

    public List<BrandResponseDto> findByVariantId(UUID variantId) {
        return repository.findByVariantId(variantId).stream()
                .map(BrandResponseDto::toDto)
                .toList();
    }

    public List<BrandResponseDto> searchByManufacturer(String manufacturer) {
        return repository.findByManufacturerContainingIgnoreCase(manufacturer).stream()
                .map(BrandResponseDto::toDto)
                .toList();
    }

//    public BrandResponseDto create(BrandDto dto) {
//        Brand entity = mapper.toEntity(dto);
//
//        Variant variant = variantRepository.findById(dto.getVariantId())
//                .orElseThrow(() -> new RuntimeException("Variant not found with id: " + dto.getVariantId()));
//        entity.setVariant(variant);
//
//        Brand saved = repository.save(entity);
//        return BrandResponseDto.toDto(saved);
//    }

    public BrandResponseDto update(UUID id, BrandDto dto) {
        Brand existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Brand not found with id: " + id));

        existing.setBrandName(dto.getBrandName());
        existing.setManufacturer(dto.getManufacturer());
        existing.setCountry(dto.getCountry());

        if (!existing.getVariant().getId().equals(dto.getVariantId())) {
            Variant variant = variantRepository.findById(dto.getVariantId())
                    .orElseThrow(() -> new RuntimeException("Variant not found with id: " + dto.getVariantId()));
            existing.setVariant(variant);
        }

        Brand updated = repository.save(existing);
        return BrandResponseDto.toDto(updated);
    }

    public void delete(UUID id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Brand not found with id: " + id);
        }
        repository.deleteById(id);
    }
}