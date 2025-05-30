package com.nexxserve.medicine.controller;

import com.nexxserve.medicine.dto.TherapeuticClassDto;
import com.nexxserve.medicine.service.TherapeuticClassService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/classes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TherapeuticClassController {

    private final TherapeuticClassService service;

    @GetMapping
    public ResponseEntity<List<TherapeuticClassDto>> getAllClasses() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TherapeuticClassDto> getClassById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<TherapeuticClassDto>> searchClasses(@RequestParam String name) {
        return ResponseEntity.ok(service.searchByName(name));
    }

    @PostMapping
    public ResponseEntity<TherapeuticClassDto> createClass(@Valid @RequestBody TherapeuticClassDto dto) {
        TherapeuticClassDto created = service.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TherapeuticClassDto> updateClass(@PathVariable UUID id, @Valid @RequestBody TherapeuticClassDto dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClass(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}