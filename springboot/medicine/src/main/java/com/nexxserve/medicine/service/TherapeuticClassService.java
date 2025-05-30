package com.nexxserve.medicine.service;

import com.nexxserve.medicine.dto.TherapeuticClassDto;
import com.nexxserve.medicine.entity.TherapeuticClass;
import com.nexxserve.medicine.mapper.MedicineMapper;
import com.nexxserve.medicine.repository.TherapeuticClassRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class TherapeuticClassService {

    private final TherapeuticClassRepository repository;
    private final MedicineMapper mapper;

    public List<TherapeuticClassDto> findAll() {
        return repository.findAll().stream()
                .map(mapper::toDto)
                .toList();
    }

    public TherapeuticClassDto findById(UUID id) {
        return repository.findById(id)
                .map(mapper::toDto)
                .orElseThrow(() -> new RuntimeException("Therapeutic class not found with id: " + id));
    }

    public List<TherapeuticClassDto> searchByName(String name) {
        return repository.findByNameContainingIgnoreCase(name).stream()
                .map(mapper::toDto)
                .toList();
    }

    public TherapeuticClassDto create(TherapeuticClassDto dto) {
        if (repository.existsByName(dto.getName())) {
            throw new RuntimeException("Therapeutic class with name '" + dto.getName() + "' already exists");
        }

        TherapeuticClass entity = mapper.toEntity(dto);
        TherapeuticClass saved = repository.save(entity);
        return mapper.toDto(saved);
    }

    public TherapeuticClassDto update(UUID id, TherapeuticClassDto dto) {
        TherapeuticClass existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Therapeutic class not found with id: " + id));

        existing.setName(dto.getName());
        existing.setDescription(dto.getDescription());

        TherapeuticClass updated = repository.save(existing);
        return mapper.toDto(updated);
    }

    public void delete(UUID id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Therapeutic class not found with id: " + id);
        }
        repository.deleteById(id);
    }
}