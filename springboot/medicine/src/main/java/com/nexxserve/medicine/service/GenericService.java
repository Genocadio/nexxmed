package com.nexxserve.medicine.service;

import com.nexxserve.medicine.dto.GenericDto;
import com.nexxserve.medicine.dto.GenericRequestDto;
import com.nexxserve.medicine.entity.Generic;
import com.nexxserve.medicine.entity.MedicineInsuranceCoverage;
import com.nexxserve.medicine.entity.TherapeuticClass;
import com.nexxserve.medicine.mapper.InsuranceCoverageMapper;
import com.nexxserve.medicine.mapper.MedicineMapper;
import com.nexxserve.medicine.repository.GenericRepository;
import com.nexxserve.medicine.repository.MedicineInsuranceCoverageRepository;
import com.nexxserve.medicine.repository.TherapeuticClassRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class GenericService {

    private final GenericRepository repository;
    private final TherapeuticClassRepository classRepository;
    private final MedicineMapper mapper;
    private final MedicineInsuranceCoverageRepository insuranceCoverageRepository;
    private final InsuranceCoverageMapper insuranceCoverageMapper;

    public List<GenericDto> findAll() {
        return repository.findAll().stream()
                .map(mapper::toDto)
                .toList();
    }

    public GenericDto findById(UUID id) {
        return repository.findById(id)
                .map(mapper::toDto)
                .orElseThrow(() -> new RuntimeException("Generic not found with id: " + id));
    }

    public List<GenericDto> searchByName(String name) {
        return repository.findByNameContainingIgnoreCase(name).stream()
                .map(mapper::toDto)
                .toList();
    }

    public List<GenericDto> findByClassId(UUID classId) {
        return repository.findByTherapeuticClassId(classId).stream()
                .map(mapper::toDto)
                .toList();
    }

    public GenericDto create(GenericDto dto) {
        Generic entity = mapper.toEntity(dto);

        if (dto.getClassId() != null) {
            TherapeuticClass therapeuticClass = classRepository.findById(dto.getClassId())
                    .orElseThrow(() -> new RuntimeException("Therapeutic class not found with id: " + dto.getClassId()));
            entity.setTherapeuticClass(therapeuticClass);
        }

        Generic saved = repository.save(entity);
        return mapper.toDto(saved);
    }

    public GenericDto create(GenericRequestDto requestDto) {
        Generic entity = mapper.toEntity(requestDto);

        if (requestDto.getClassId() != null) {
            TherapeuticClass therapeuticClass = classRepository.findById(requestDto.getClassId())
                    .orElseThrow(() -> new RuntimeException("Therapeutic class not found with id: " + requestDto.getClassId()));
            entity.setTherapeuticClass(therapeuticClass);
        }

        Generic saved = repository.save(entity);

        // Handle insurance coverages if present
        saveInsuranceCoverages(requestDto, saved);

        return findById(saved.getId());
    }

    public GenericDto update(UUID id, GenericRequestDto requestDto) {
        Generic existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Generic not found with id: " + requestDto.getId()));

        existing.setName(requestDto.getName());
        existing.setChemicalName(requestDto.getChemicalName());
        existing.setDescription(requestDto.getDescription());
        existing.setIsParent(requestDto.getIsParent());

        if (requestDto.getClassId() != null) {
            TherapeuticClass therapeuticClass = classRepository.findById(requestDto.getClassId())
                    .orElseThrow(() -> new RuntimeException("Therapeutic class not found with id: " + requestDto.getClassId()));
            existing.setTherapeuticClass(therapeuticClass);
        }
        saveInsuranceCoverages(requestDto, existing);
        Generic updated = repository.save(existing);
        return mapper.toDto(updated);
    }

  private void saveInsuranceCoverages(GenericRequestDto requestDto, Generic existing) {
      // Delete all existing insurance coverages for this generic
      insuranceCoverageRepository.deleteByGenericId(existing.getId());

      // Add new insurance coverages if present
      if (requestDto.getInsuranceCoverages() != null && !requestDto.getInsuranceCoverages().isEmpty()) {
          List<MedicineInsuranceCoverage> coverages = new ArrayList<>();

          for (var coverageDto : requestDto.getInsuranceCoverages()) {
              MedicineInsuranceCoverage coverage = insuranceCoverageMapper.toEntity(coverageDto);
              coverage.setGeneric(existing);
              coverage.setCreatedBy("system");
              coverage.setUpdatedBy("system");
              coverages.add(coverage);
          }
          insuranceCoverageRepository.saveAll(coverages);
      }
      // If insurance coverages are null or empty, we've already deleted the existing ones
  }

    public GenericDto update(UUID id, GenericDto dto) {
        Generic existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Generic not found with id: " + id));

        existing.setName(dto.getName());
        existing.setChemicalName(dto.getChemicalName());
        existing.setDescription(dto.getDescription());
        existing.setIsParent(dto.getIsParent());

        if (dto.getClassId() != null) {
            TherapeuticClass therapeuticClass = classRepository.findById(dto.getClassId())
                    .orElseThrow(() -> new RuntimeException("Therapeutic class not found with id: " + dto.getClassId()));
            existing.setTherapeuticClass(therapeuticClass);
        }

        Generic updated = repository.save(existing);
        return mapper.toDto(updated);
    }

    public void delete(UUID id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Generic not found with id: " + id);
        }
        repository.deleteById(id);
    }
}