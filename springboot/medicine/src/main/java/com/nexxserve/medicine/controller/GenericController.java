package com.nexxserve.medicine.controller;

import com.nexxserve.medicine.dto.GenericDto;
import com.nexxserve.medicine.dto.GenericRequestDto;
import com.nexxserve.medicine.service.GenericService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/generics")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class GenericController {

    private final GenericService service;

    @GetMapping
    public ResponseEntity<List<GenericDto>> getAllGenerics() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GenericDto> getGenericById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<GenericDto>> searchGenerics(@RequestParam String name) {
        return ResponseEntity.ok(service.searchByName(name));
    }

    @GetMapping("/by-class/{classId}")
    public ResponseEntity<List<GenericDto>> getGenericsByClass(@PathVariable UUID classId) {
        return ResponseEntity.ok(service.findByClassId(classId));
    }

    @PostMapping
    public ResponseEntity<GenericDto> createGeneric(@Valid @RequestBody GenericDto dto) {
        GenericDto created = service.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GenericDto> updateGeneric(@PathVariable UUID id, @Valid @RequestBody GenericRequestDto dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGeneric(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}