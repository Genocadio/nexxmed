package com.nexxserve.catalog.controller;

import com.nexxserve.catalog.dto.ProductFamilyDto;
import com.nexxserve.catalog.dto.ProductFamilyRequestDto;
import com.nexxserve.catalog.enums.ProductStatus;
import com.nexxserve.catalog.service.ProductFamilyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/product-families")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ProductFamilyController {

    private final ProductFamilyService productFamilyService;

    @GetMapping("/{id}")
    public ResponseEntity<ProductFamilyDto> findById(@PathVariable UUID id) {
        log.debug("REST request to get ProductFamily: {}", id);
        ProductFamilyDto productFamily = productFamilyService.findById(id);
        return ResponseEntity.ok(productFamily);
    }

    @GetMapping
    public ResponseEntity<Page<ProductFamilyDto>> findAll(Pageable pageable) {
        log.debug("REST request to get all ProductFamilies");
        Page<ProductFamilyDto> page = productFamilyService.findAll(pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<Page<ProductFamilyDto>> findByStatus(
            @PathVariable ProductStatus status,
            Pageable pageable) {
        log.debug("REST request to get ProductFamilies by status: {}", status);
        Page<ProductFamilyDto> page = productFamilyService.findByStatus(status, pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/brand/{brand}")
    public ResponseEntity<List<ProductFamilyDto>> findByBrand(@PathVariable String brand) {
        log.debug("REST request to get ProductFamilies by brand: {}", brand);
        List<ProductFamilyDto> productFamilies = productFamilyService.findByBrand(brand);
        return ResponseEntity.ok(productFamilies);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ProductFamilyDto>> search(
            @RequestParam String term,
            Pageable pageable) {
        log.debug("REST request to search ProductFamilies by term: {}", term);
        Page<ProductFamilyDto> page = productFamilyService.searchByTerm(term, pageable);
        return ResponseEntity.ok(page);
    }

    @PostMapping
    public ResponseEntity<ProductFamilyDto> create(
            @Valid @RequestBody ProductFamilyRequestDto productFamilyDto,
            @RequestHeader(value = "X-User-ID", defaultValue = "system") String userId) {
        log.debug("REST request to save ProductFamily: {}", productFamilyDto);
        ProductFamilyDto result = productFamilyService.create(productFamilyDto, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductFamilyDto> update(
            @PathVariable UUID id,
            @Valid @RequestBody ProductFamilyRequestDto productFamilyDto,
            @RequestHeader(value = "X-User-ID", defaultValue = "system") String userId) {
        log.debug("REST request to update ProductFamily: {}", id);
        ProductFamilyDto result = productFamilyService.update(id, productFamilyDto, userId);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        log.debug("REST request to delete ProductFamily: {}", id);
        productFamilyService.delete(id);
        return ResponseEntity.noContent().build();
    }
}