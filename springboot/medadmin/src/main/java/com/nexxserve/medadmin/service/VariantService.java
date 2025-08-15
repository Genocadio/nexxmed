package com.nexxserve.medadmin.service;

import com.nexxserve.medadmin.dto.medicine.BrandMedicineDetails;
import com.nexxserve.medadmin.dto.medicine.VariantRequestDto;
import com.nexxserve.medadmin.dto.medicine.VariantResponseDto;
import com.nexxserve.medadmin.entity.medicine.Generic;
import com.nexxserve.medadmin.entity.medicine.MedicineInsuranceCoverage;
import com.nexxserve.medadmin.entity.medicine.Variant;
import com.nexxserve.medadmin.mapper.InsuranceCoverageMapper;
import com.nexxserve.medadmin.repository.medicine.GenericRepository;
import com.nexxserve.medadmin.repository.medicine.MedicineInsuranceCoverageRepository;
import com.nexxserve.medadmin.repository.medicine.VariantRepository;
import com.nexxserve.medadmin.service.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class VariantService {

    private final VariantRepository repository;
    private final JwtService jwtService;
    private final GenericRepository genericRepository;
    private final MedicineInsuranceCoverageRepository insuranceCoverageRepository;
    private final InsuranceCoverageMapper insuranceCoverageMapper;
    public List<VariantResponseDto> findAll() {
        return repository.findAll().stream()
                .map(VariantResponseDto::fromEntity)
                .toList();
    }

    public VariantResponseDto findById(UUID id) {
        return repository.findById(id)
                .map(VariantResponseDto::fromEntity)
                .orElseThrow(() -> new RuntimeException("Variant not found with id: " + id));
    }

    public List<VariantResponseDto> searchByName(String name) {
        return repository.findByNameContainingIgnoreCase(name).stream()
                .map(VariantResponseDto::fromEntity)
                .toList();
    }

    public List<VariantResponseDto> findByGenericId(UUID genericId) {
        return repository.findByGenericId(genericId).stream()
                .map(VariantResponseDto::fromEntity)
                .toList();
    }

    public List<VariantResponseDto> searchByForm(String form) {
        return repository.findByFormContainingIgnoreCase(form).stream()
                .map(VariantResponseDto::fromEntity)
                .toList();
    }

    public List<VariantResponseDto> searchByRoute(String route) {
        return repository.findByRouteContainingIgnoreCase(route).stream()
                .map(VariantResponseDto::fromEntity)
                .toList();
    }

    public VariantResponseDto create(VariantRequestDto requestDto) {
        Variant entity = requestDto.toEntity();

        if (requestDto.getGenericIds() != null && !requestDto.getGenericIds().isEmpty()) {
            List<Generic> generics = genericRepository.findAllById(requestDto.getGenericIds());
            if (generics.size() != requestDto.getGenericIds().size()) {
                throw new RuntimeException("One or more generic IDs not found");
            }
            entity.setGenerics(generics);
        }

        Variant saved = repository.save(entity);

        // Handle insurance coverages if present
        if (requestDto.getInsuranceCoverages() != null && !requestDto.getInsuranceCoverages().isEmpty()) {
            List<MedicineInsuranceCoverage> coverages = new ArrayList<>();

            for (var coverageDto : requestDto.getInsuranceCoverages()) {
                MedicineInsuranceCoverage coverage = insuranceCoverageMapper.toEntity(coverageDto);
                coverage.setVariant(saved);
                coverages.add(coverage);
                coverage.setCreatedBy(jwtService.getCurrentAdminId().toString());
                coverage.setUpdatedBy(jwtService.getCurrentAdminId().toString());
            }


            insuranceCoverageRepository.saveAll(coverages);
        }

        return findById(saved.getId());
    }

    public VariantResponseDto update(UUID id, BrandMedicineDetails.VariantRequestDto requestDto) {
        Variant existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Variant not found with id: " + requestDto.getId()));

        existing.setName(requestDto.getName());
        existing.setTradeName(requestDto.getTradeName());
        existing.setForm(requestDto.getForm());
        existing.setRoute(requestDto.getRoute());
        existing.setStrength(requestDto.getStrength());
        existing.setConcentration(requestDto.getConcentration());
        existing.setPackaging(requestDto.getPackaging());
        existing.setNotes(requestDto.getNotes());

        if (requestDto.getGenericIds() != null) {
            List<Generic> generics = genericRepository.findAllById(requestDto.getGenericIds());
            if (generics.size() != requestDto.getGenericIds().size()) {
                throw new RuntimeException("One or more generic IDs not found");
            }
            existing.setGenerics(generics);
        }

        Variant updated = repository.save(existing);
        insuranceCoverageRepository.deleteByVariantId(updated.getId());

        // Handle insurance coverages if present
        if (requestDto.getInsuranceCoverages() != null && !requestDto.getInsuranceCoverages().isEmpty()) {
            insuranceCoverageRepository.deleteByVariantId(updated.getId());

            List<MedicineInsuranceCoverage> coverages = new ArrayList<>();
            for (var coverageDto : requestDto.getInsuranceCoverages()) {
                MedicineInsuranceCoverage coverage = insuranceCoverageMapper.toEntity(coverageDto);
                coverage.setCreatedBy("system"); // Assuming system user for simplicity
                coverage.setUpdatedBy("system"); // Assuming system user for simplicity
                coverage.setVariant(updated);
                coverages.add(coverage);
            }
            insuranceCoverageRepository.saveAll(coverages);
        }

        return findById(updated.getId());
    }

//    public VariantResponseDto create(VariantRequestDto dto) {
//        Variant entity = dto.toEntity()
//
//        if (dto.getGenericIds() != null && !dto.getGenericIds().isEmpty()) {
//            List<Generic> generics = genericRepository.findAllById(dto.getGenericIds());
//            if (generics.size() != dto.getGenericIds().size()) {
//                throw new RuntimeException("One or more generic IDs not found");
//            }
//            entity.setGenerics(generics);
//        }
//
//        Variant saved = repository.save(entity);
//        return VariantResponseDto.fromEntity(saved);
//    }

    public VariantResponseDto update(UUID id, VariantRequestDto dto) {
        Variant existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Variant not found with id: " + id));

        existing.setName(dto.getName());
        existing.setTradeName(dto.getTradeName());
        existing.setForm(dto.getForm());
        existing.setRoute(dto.getRoute());
        existing.setStrength(dto.getStrength());
        existing.setConcentration(dto.getConcentration());
        existing.setPackaging(dto.getPackaging());
        existing.setNotes(dto.getNotes());

        if (dto.getGenericIds() != null) {
            List<Generic> generics = genericRepository.findAllById(dto.getGenericIds());
            if (generics.size() != dto.getGenericIds().size()) {
                throw new RuntimeException("One or more generic IDs not found");
            }
            existing.setGenerics(generics);
        }

        Variant updated = repository.save(existing);
        return VariantResponseDto.fromEntity(updated);
    }

    public void delete(UUID id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Variant not found with id: " + id);
        }
        repository.deleteById(id);
    }
}
