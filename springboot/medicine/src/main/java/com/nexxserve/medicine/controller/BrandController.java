package com.nexxserve.medicine.controller;

import com.nexxserve.medicine.dto.BrandDto;
import com.nexxserve.medicine.dto.BrandRequestDto;
import com.nexxserve.medicine.service.BrandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/brands")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BrandController {

    private final BrandService service;

    @GetMapping
    public ResponseEntity<List<BrandDto>> getAllBrands() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BrandDto> getBrandById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<BrandDto>> searchBrands(@RequestParam String brandName) {
        return ResponseEntity.ok(service.searchByBrandName(brandName));
    }

    @GetMapping("/by-variant/{variantId}")
    public ResponseEntity<List<BrandDto>> getBrandsByVariant(@PathVariable UUID variantId) {
        return ResponseEntity.ok(service.findByVariantId(variantId));
    }

    @GetMapping("/by-manufacturer")
    public ResponseEntity<List<BrandDto>> searchBrandsByManufacturer(@RequestParam String manufacturer) {
        return ResponseEntity.ok(service.searchByManufacturer(manufacturer));
    }

    @PostMapping
    public ResponseEntity<BrandDto> createBrand(@Valid @RequestBody BrandDto dto) {
        BrandDto created = service.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BrandDto> updateBrand(@PathVariable UUID id, @Valid @RequestBody BrandRequestDto dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBrand(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}