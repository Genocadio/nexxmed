package com.nexxserve.medadmin.service;

import com.nexxserve.medadmin.dto.medicine.TherapeuticClassDto;
import com.nexxserve.medadmin.entity.medicine.TherapeuticClass;
import com.nexxserve.medadmin.repository.medicine.TherapeuticClassRepository;
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

    public List<TherapeuticClassDto> findAll() {
        return repository.findAll().stream()
                .map(TherapeuticClassDto::fromEntity)
                .toList();
    }

    public TherapeuticClassDto findById(UUID id) {
        return repository.findById(id)
                .map(TherapeuticClassDto::fromEntity)
                .orElseThrow(() -> new RuntimeException("Therapeutic class not found with id: " + id));
    }

    public List<TherapeuticClassDto> searchByName(String name) {
        return repository.findByNameContainingIgnoreCase(name).stream()
                .map(TherapeuticClassDto::fromEntity)
                .toList();
    }

    public TherapeuticClassDto create(TherapeuticClassDto dto) {
        if (repository.existsByName(dto.getName())) {
            throw new RuntimeException("Therapeutic class with name '" + dto.getName() + "' already exists");
        }

        TherapeuticClass entity = dto.toEntity();
        TherapeuticClass saved = repository.save(entity);
        return TherapeuticClassDto.fromEntity(saved);
    }

    public TherapeuticClassDto update(UUID id, TherapeuticClassDto dto) {
        TherapeuticClass existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Therapeutic class not found with id: " + id));

        existing.setName(dto.getName());
        existing.setDescription(dto.getDescription());

        TherapeuticClass updated = repository.save(existing);
        return TherapeuticClassDto.fromEntity(updated);
    }

    public void delete(UUID id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Therapeutic class not found with id: " + id);
        }
        repository.deleteById(id);
    }
}