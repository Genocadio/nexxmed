package com.nexxserve.medicine.service;

import com.nexxserve.medicine.dto.BrandDto;
import com.nexxserve.medicine.dto.BrandRequestDto;
import com.nexxserve.medicine.entity.Brand;
import com.nexxserve.medicine.entity.MedicineInsuranceCoverage;
import com.nexxserve.medicine.entity.Variant;
import com.nexxserve.medicine.mapper.InsuranceCoverageMapper;
import com.nexxserve.medicine.mapper.MedicineMapper;
import com.nexxserve.medicine.repository.BrandRepository;
import com.nexxserve.medicine.repository.MedicineInsuranceCoverageRepository;
import com.nexxserve.medicine.repository.VariantRepository;
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
    private final MedicineMapper mapper;
    private final InsuranceCoverageMapper insuranceCoverageMapper;
    private final MedicineInsuranceCoverageRepository insuranceCoverageRepository;


    public BrandDto create(BrandRequestDto requestDto) {
        Brand entity = mapper.toEntity(requestDto);

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

    public BrandDto update(UUID id, BrandRequestDto requestDto) {
        Brand existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Brand not found with id: " + requestDto.getId()));

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


    public List<BrandDto> findAll() {
        return repository.findAll().stream()
                .map(mapper::toDto)
                .toList();
    }

    public BrandDto findById(UUID id) {
        return repository.findById(id)
                .map(mapper::toDto)
                .orElseThrow(() -> new RuntimeException("Brand not found with id: " + id));
    }

    public List<BrandDto> searchByBrandName(String brandName) {
        return repository.findByBrandNameContainingIgnoreCase(brandName).stream()
                .map(mapper::toDto)
                .toList();
    }

    public List<BrandDto> findByVariantId(UUID variantId) {
        return repository.findByVariantId(variantId).stream()
                .map(mapper::toDto)
                .toList();
    }

    public List<BrandDto> searchByManufacturer(String manufacturer) {
        return repository.findByManufacturerContainingIgnoreCase(manufacturer).stream()
                .map(mapper::toDto)
                .toList();
    }

    public BrandDto create(BrandDto dto) {
        Brand entity = mapper.toEntity(dto);

        Variant variant = variantRepository.findById(dto.getVariantId())
                .orElseThrow(() -> new RuntimeException("Variant not found with id: " + dto.getVariantId()));
        entity.setVariant(variant);

        Brand saved = repository.save(entity);
        return mapper.toDto(saved);
    }

    public BrandDto update(UUID id, BrandDto dto) {
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
        return mapper.toDto(updated);
    }

    public void delete(UUID id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Brand not found with id: " + id);
        }
        repository.deleteById(id);
    }
}