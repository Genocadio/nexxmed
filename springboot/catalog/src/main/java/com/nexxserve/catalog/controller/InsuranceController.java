package com.nexxserve.catalog.controller;

import com.nexxserve.catalog.dto.*;
import com.nexxserve.catalog.enums.InsuranceStatus;
import com.nexxserve.catalog.service.InsuranceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/insurances")
@RequiredArgsConstructor
@Slf4j
public class InsuranceController {

    private final InsuranceService insuranceService;

    @GetMapping
    public ResponseEntity<Page<InsuranceDto>> getAllInsurances(Pageable pageable) {
        Page<InsuranceDto> insurances = insuranceService.findAll(pageable);
        return ResponseEntity.ok(insurances);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InsuranceDto> getInsuranceById(@PathVariable UUID id) {
        InsuranceDto insurance = insuranceService.findById(id);
        return ResponseEntity.ok(insurance);
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<InsuranceDto> getInsuranceByCode(@PathVariable String code) {
        InsuranceDto insurance = insuranceService.findByCode(code);
        return ResponseEntity.ok(insurance);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<InsuranceDto>> getInsurancesByStatus(@PathVariable InsuranceStatus status) {
        List<InsuranceDto> insurances = insuranceService.findByStatus(status);
        return ResponseEntity.ok(insurances);
    }

    @PostMapping
    public ResponseEntity<InsuranceDto> createInsurance(
            @Valid @RequestBody InsuranceRequestDto insuranceDto,
            @RequestHeader("X-User-ID") String userId) {
        InsuranceDto createdInsurance = insuranceService.create(insuranceDto, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdInsurance);
    }

    @PutMapping("/{id}")
    public ResponseEntity<InsuranceDto> updateInsurance(
            @PathVariable UUID id,
            @Valid @RequestBody InsuranceRequestDto insuranceDto,
            @RequestHeader("X-User-ID") String userId) {
        InsuranceDto updatedInsurance = insuranceService.update(id, insuranceDto, userId);
        return ResponseEntity.ok(updatedInsurance);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInsurance(@PathVariable UUID id) {
        insuranceService.delete(id);
        return ResponseEntity.noContent().build();
    }
}