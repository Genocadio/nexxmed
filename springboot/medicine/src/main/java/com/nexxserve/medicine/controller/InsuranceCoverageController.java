package com.nexxserve.medicine.controller;

import com.nexxserve.medicine.dto.InsuranceCoverageRequestDto;
import com.nexxserve.medicine.dto.InsuranceCoverageResponseDto;
import com.nexxserve.medicine.service.InsuranceCoverageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/insurance-coverage")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class InsuranceCoverageController {

    private final InsuranceCoverageService service;

    @GetMapping
    public ResponseEntity<List<InsuranceCoverageResponseDto>> getAllCoverages() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<InsuranceCoverageResponseDto> getCoverageById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping("/insurance/{insuranceId}")
    public ResponseEntity<List<InsuranceCoverageResponseDto>> getCoveragesByInsurance(@PathVariable UUID insuranceId) {
        return ResponseEntity.ok(service.findByInsuranceId(insuranceId));
    }

    @GetMapping("/generic/{genericId}")
    public ResponseEntity<List<InsuranceCoverageResponseDto>> getCoveragesByGeneric(@PathVariable UUID genericId) {
        return ResponseEntity.ok(service.findByGenericId(genericId));
    }

    @GetMapping("/brand/{brandId}")
    public ResponseEntity<List<InsuranceCoverageResponseDto>> getCoveragesByBrand(@PathVariable UUID brandId) {
        return ResponseEntity.ok(service.findByBrandId(brandId));
    }

    @GetMapping("/variant/{variantId}")
    public ResponseEntity<List<InsuranceCoverageResponseDto>> getCoveragesByVariant(@PathVariable UUID variantId) {
        return ResponseEntity.ok(service.findByVariantId(variantId));
    }

    @PostMapping
    public ResponseEntity<InsuranceCoverageResponseDto> createCoverage(
            @Valid @RequestBody InsuranceCoverageRequestDto requestDto,
            @AuthenticationPrincipal UserDetails userDetails) {
        InsuranceCoverageResponseDto created = service.create(requestDto, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<InsuranceCoverageResponseDto> updateCoverage(
            @PathVariable UUID id,
            @Valid @RequestBody InsuranceCoverageRequestDto requestDto,
            @AuthenticationPrincipal UserDetails userDetails) {
        InsuranceCoverageResponseDto updated = service.update(id, requestDto, userDetails.getUsername());
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCoverage(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}