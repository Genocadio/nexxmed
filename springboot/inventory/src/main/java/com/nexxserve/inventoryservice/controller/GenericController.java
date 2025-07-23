package com.nexxserve.inventoryservice.controller;

import com.nexxserve.inventoryservice.dto.medicine.GenericDto;
import com.nexxserve.inventoryservice.dto.medicine.GenericRequestDto;
import com.nexxserve.inventoryservice.dto.medicine.GenericResponseDto;
import com.nexxserve.inventoryservice.service.GenericService;
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
public class GenericController {

    private final GenericService service;

    @GetMapping
    public ResponseEntity<List<GenericResponseDto>> getAllGenerics() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GenericResponseDto> getGenericById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<GenericResponseDto>> searchGenerics(@RequestParam String name) {
        return ResponseEntity.ok(service.searchByName(name));
    }

    @GetMapping("/by-class/{classId}")
    public ResponseEntity<List<GenericResponseDto>> getGenericsByClass(@PathVariable UUID classId) {
        return ResponseEntity.ok(service.findByClassId(classId));
    }

    @PostMapping
    public ResponseEntity<GenericResponseDto> createGeneric(@Valid @RequestBody GenericRequestDto dto) {
        GenericResponseDto created = service.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GenericResponseDto> updateGeneric(@PathVariable UUID id, @Valid @RequestBody GenericRequestDto dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGeneric(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}