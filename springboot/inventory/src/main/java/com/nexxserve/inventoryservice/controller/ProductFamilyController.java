package com.nexxserve.inventoryservice.controller;

import com.nexxserve.inventoryservice.dto.catalog.ProductFamilyDto;
import com.nexxserve.inventoryservice.dto.catalog.ProductFamilyRequestDto;
import com.nexxserve.inventoryservice.service.ProductFamilyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/product-families")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ProductFamilyController {

    private final ProductFamilyService productFamilyService;


    @GetMapping("/{id}")
    public ResponseEntity<ProductFamilyDto> findById(@PathVariable UUID id) {
        log.debug("REST request to get ProductFamily: {}", id);
        ProductFamilyDto productFamily = productFamilyService.getProductFamilyById(id);
        return ResponseEntity.ok(productFamily);
    }

    @GetMapping
    public ResponseEntity<Page<ProductFamilyDto>> findAll(Pageable pageable) {
        log.debug("REST request to get all ProductFamilies");
        Page families = productFamilyService.getAllProductFamilies(pageable);
        return ResponseEntity.ok(families);
    }

    @PostMapping
    public ResponseEntity<ProductFamilyDto> create(
            @Valid @RequestBody ProductFamilyRequestDto productFamilyDto,
            @RequestHeader(value = "X-User-ID", defaultValue = "system") String userId) {
        log.debug("REST request to save ProductFamily: {}", productFamilyDto);
        ProductFamilyDto result = productFamilyService.createProductFamily(productFamilyDto, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductFamilyDto> update(
            @PathVariable UUID id,
            @Valid @RequestBody ProductFamilyRequestDto productFamilyDto,
            @RequestHeader(value = "X-User-ID", defaultValue = "system") String userId) {
        log.debug("REST request to update ProductFamily: {}", id);
        ProductFamilyDto result = productFamilyService.updateProductFamily(id, productFamilyDto, userId);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        log.debug("REST request to delete ProductFamily: {}", id);
        productFamilyService.deleteProductFamily(id);
        return ResponseEntity.noContent().build();
    }
}