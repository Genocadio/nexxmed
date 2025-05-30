package com.nexxserve.medicine.service;

import com.nexxserve.medicine.dto.VariantDto;
import com.nexxserve.medicine.dto.VariantRequestDto;
import com.nexxserve.medicine.entity.Generic;
import com.nexxserve.medicine.entity.MedicineInsuranceCoverage;
import com.nexxserve.medicine.entity.Variant;
import com.nexxserve.medicine.mapper.InsuranceCoverageMapper;
import com.nexxserve.medicine.mapper.MedicineMapper;
import com.nexxserve.medicine.repository.GenericRepository;
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
public class VariantService {

    private final VariantRepository repository;
    private final GenericRepository genericRepository;
    private final MedicineMapper mapper;
    private final MedicineInsuranceCoverageRepository insuranceCoverageRepository;
    private final InsuranceCoverageMapper insuranceCoverageMapper;
    public List<VariantDto> findAll() {
        return repository.findAll().stream()
                .map(mapper::toDto)
                .toList();
    }

    public VariantDto findById(UUID id) {
        return repository.findById(id)
                .map(mapper::toDto)
                .orElseThrow(() -> new RuntimeException("Variant not found with id: " + id));
    }

    public List<VariantDto> searchByName(String name) {
        return repository.findByNameContainingIgnoreCase(name).stream()
                .map(mapper::toDto)
                .toList();
    }

    public List<VariantDto> findByGenericId(UUID genericId) {
        return repository.findByGenericId(genericId).stream()
                .map(mapper::toDto)
                .toList();
    }

    public List<VariantDto> searchByForm(String form) {
        return repository.findByFormContainingIgnoreCase(form).stream()
                .map(mapper::toDto)
                .toList();
    }

    public List<VariantDto> searchByRoute(String route) {
        return repository.findByRouteContainingIgnoreCase(route).stream()
                .map(mapper::toDto)
                .toList();
    }

    public VariantDto create(VariantRequestDto requestDto) {
        Variant entity = mapper.toEntity(requestDto);

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
            }

            insuranceCoverageRepository.saveAll(coverages);
        }

        return findById(saved.getId());
    }

    public VariantDto update(UUID id, VariantRequestDto requestDto) {
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
        existing.setExtraInfo(requestDto.getExtraInfo());

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

    public VariantDto create(VariantDto dto) {
        Variant entity = mapper.toEntity(dto);

        if (dto.getGenericIds() != null && !dto.getGenericIds().isEmpty()) {
            List<Generic> generics = genericRepository.findAllById(dto.getGenericIds());
            if (generics.size() != dto.getGenericIds().size()) {
                throw new RuntimeException("One or more generic IDs not found");
            }
            entity.setGenerics(generics);
        }

        Variant saved = repository.save(entity);
        return mapper.toDto(saved);
    }

    public VariantDto update(UUID id, VariantDto dto) {
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
        existing.setExtraInfo(dto.getExtraInfo());

        if (dto.getGenericIds() != null) {
            List<Generic> generics = genericRepository.findAllById(dto.getGenericIds());
            if (generics.size() != dto.getGenericIds().size()) {
                throw new RuntimeException("One or more generic IDs not found");
            }
            existing.setGenerics(generics);
        }

        Variant updated = repository.save(existing);
        return mapper.toDto(updated);
    }

    public void delete(UUID id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Variant not found with id: " + id);
        }
        repository.deleteById(id);
    }
}
