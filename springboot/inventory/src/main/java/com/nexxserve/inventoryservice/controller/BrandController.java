package com.nexxserve.inventoryservice.controller;

import com.nexxserve.inventoryservice.dto.medicine.BrandDto;
import com.nexxserve.inventoryservice.dto.medicine.BrandRequestDto;
import com.nexxserve.inventoryservice.dto.medicine.BrandResponseDto;
import com.nexxserve.inventoryservice.service.BrandService;
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
public class BrandController {

    private final BrandService service;

    @GetMapping
    public ResponseEntity<List<BrandResponseDto>> getAllBrands() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BrandResponseDto> getBrandById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<BrandResponseDto>> searchBrands(@RequestParam String brandName) {
        return ResponseEntity.ok(service.searchByBrandName(brandName));
    }

    @GetMapping("/by-variant/{variantId}")
    public ResponseEntity<List<BrandResponseDto>> getBrandsByVariant(@PathVariable UUID variantId) {
        return ResponseEntity.ok(service.findByVariantId(variantId));
    }

    @GetMapping("/by-manufacturer")
    public ResponseEntity<List<BrandResponseDto>> searchBrandsByManufacturer(@RequestParam String manufacturer) {
        return ResponseEntity.ok(service.searchByManufacturer(manufacturer));
    }

    @PostMapping
    public ResponseEntity<BrandResponseDto> createBrand(@Valid @RequestBody BrandRequestDto dto) {
        BrandResponseDto created = service.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BrandResponseDto> updateBrand(@PathVariable UUID id, @Valid @RequestBody BrandRequestDto dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBrand(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}