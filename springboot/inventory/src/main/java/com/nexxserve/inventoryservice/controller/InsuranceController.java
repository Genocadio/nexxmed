package com.nexxserve.inventoryservice.controller;

import com.nexxserve.inventoryservice.dto.Insurance.InsuranceRequestDto;
import com.nexxserve.inventoryservice.dto.Insurance.InsuranceResponseDto;
import com.nexxserve.inventoryservice.entity.Insurance;
import com.nexxserve.inventoryservice.service.InsuranceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/insurances")
public class InsuranceController {

    private final InsuranceService insuranceService;

    @Autowired
    public InsuranceController(InsuranceService insuranceService) {
        this.insuranceService = insuranceService;
    }

    @PostMapping
    public ResponseEntity<InsuranceResponseDto> createInsurance(
            @RequestBody InsuranceRequestDto requestDto,
            Principal principal) {
        String username = (principal != null) ? principal.getName() : "system";
        Insurance insurance = insuranceService.createInsurance(requestDto,username);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(InsuranceResponseDto.fromEntity(insurance));
    }

    @PutMapping("/{id}")
    public ResponseEntity<InsuranceResponseDto> updateInsurance(
            @PathVariable UUID id,
            @RequestBody InsuranceRequestDto requestDto,
            Principal principal) {
        String username = (principal != null) ? principal.getName() : "system";
        Insurance insurance = insuranceService.updateInsurance(id, requestDto, username);
        if (insurance != null) {
            return ResponseEntity.ok(InsuranceResponseDto.fromEntity(insurance));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInsurance(@PathVariable UUID id) {
        insuranceService.deleteInsurance(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<InsuranceResponseDto> getInsuranceById(@PathVariable UUID id) {
        Optional<Insurance> insurance = insuranceService.findById(id);
        return insurance.map(i -> ResponseEntity.ok(InsuranceResponseDto.fromEntity(i)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<InsuranceResponseDto>> getAllInsurances() {
        List<Insurance> insurances = insuranceService.findAll();
        List<InsuranceResponseDto> responseDtos = insurances.stream()
                .map(InsuranceResponseDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseDtos);
    }

    @GetMapping("/search")
    public ResponseEntity<List<InsuranceResponseDto>> searchInsurances(@RequestParam String query) {
        List<Insurance> insurances = insuranceService.search(query);
        List<InsuranceResponseDto> responseDtos = insurances.stream()
                .map(InsuranceResponseDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseDtos);
    }

    @GetMapping("/by-name/{name}")
    public ResponseEntity<InsuranceResponseDto> getInsuranceByName(@PathVariable String name) {
        Optional<Insurance> insurance = insuranceService.findByName(name);
        return insurance.map(i -> ResponseEntity.ok(InsuranceResponseDto.fromEntity(i)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/by-status")
    public ResponseEntity<List<InsuranceResponseDto>> getInsurancesByStatus(@RequestParam Boolean active) {
        List<Insurance> insurances = insuranceService.findByActive(active);
        List<InsuranceResponseDto> responseDtos = insurances.stream()
                .map(InsuranceResponseDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseDtos);
    }
}