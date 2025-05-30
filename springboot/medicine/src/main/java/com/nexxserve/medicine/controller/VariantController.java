package com.nexxserve.medicine.controller;

import com.nexxserve.medicine.dto.VariantDto;
import com.nexxserve.medicine.dto.VariantRequestDto;
import com.nexxserve.medicine.service.VariantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/variants")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class VariantController {

    private final VariantService service;

    @GetMapping
    public ResponseEntity<List<VariantDto>> getAllVariants() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<VariantDto> getVariantById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<VariantDto>> searchVariants(@RequestParam String name) {
        return ResponseEntity.ok(service.searchByName(name));
    }

    @GetMapping("/by-generic/{genericId}")
    public ResponseEntity<List<VariantDto>> getVariantsByGeneric(@PathVariable UUID genericId) {
        return ResponseEntity.ok(service.findByGenericId(genericId));
    }

    @GetMapping("/by-form")
    public ResponseEntity<List<VariantDto>> searchVariantsByForm(@RequestParam String form) {
        return ResponseEntity.ok(service.searchByForm(form));
    }

    @GetMapping("/by-route")
    public ResponseEntity<List<VariantDto>> searchVariantsByRoute(@RequestParam String route) {
        return ResponseEntity.ok(service.searchByRoute(route));
    }

    @PostMapping
    public ResponseEntity<VariantDto> createVariant(@Valid @RequestBody VariantDto dto) {
        VariantDto created = service.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<VariantDto> updateVariant(@PathVariable UUID id, @Valid @RequestBody VariantRequestDto dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVariant(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}