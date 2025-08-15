package com.nexxserve.inventoryservice.controller;

import com.nexxserve.inventoryservice.dto.medicine.BrandMedicineDetails;
import com.nexxserve.inventoryservice.dto.medicine.VariantRequestDto;
import com.nexxserve.inventoryservice.dto.medicine.VariantResponseDto;
import com.nexxserve.inventoryservice.service.VariantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/variants")
@RequiredArgsConstructor
public class VariantController {

    private final VariantService service;

    @GetMapping
    public ResponseEntity<List<VariantResponseDto>> getAllVariants() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<VariantResponseDto> getVariantById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<VariantResponseDto>> searchVariants(@RequestParam String name) {
        return ResponseEntity.ok(service.searchByName(name));
    }

    @GetMapping("/by-generic/{genericId}")
    public ResponseEntity<List<VariantResponseDto>> getVariantsByGeneric(@PathVariable UUID genericId) {
        return ResponseEntity.ok(service.findByGenericId(genericId));
    }

    @GetMapping("/by-form")
    public ResponseEntity<List<VariantResponseDto>> searchVariantsByForm(@RequestParam String form) {
        return ResponseEntity.ok(service.searchByForm(form));
    }

    @GetMapping("/by-route")
    public ResponseEntity<List<VariantResponseDto>> searchVariantsByRoute(@RequestParam String route) {
        return ResponseEntity.ok(service.searchByRoute(route));
    }

    @PostMapping
    public ResponseEntity<VariantResponseDto> createVariant(@Valid @RequestBody VariantRequestDto dto) {
        VariantResponseDto created = service.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<VariantResponseDto> updateVariant(@PathVariable UUID id, @Valid @RequestBody BrandMedicineDetails.VariantRequestDto dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVariant(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}