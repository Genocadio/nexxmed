package com.nexxserve.catalog.controller;

import com.nexxserve.catalog.dto.ProductVariantDto;
import com.nexxserve.catalog.dto.ProductVariantRequestDto;
import com.nexxserve.catalog.enums.ProductStatus;
import com.nexxserve.catalog.service.ProductVariantService;
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
@RequestMapping("/product-variants")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ProductVariantController {

    private final ProductVariantService productVariantService;

    @GetMapping
    public ResponseEntity<Page<ProductVariantDto>> findAll(Pageable pageable) {
        log.debug("REST request to get all ProductVariants with pagination: {}", pageable);
        Page<ProductVariantDto> page = productVariantService.findAll(pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductVariantDto> findById(@PathVariable UUID id) {
        log.debug("REST request to get ProductVariant: {}", id);
        ProductVariantDto productVariant = productVariantService.findById(id);
        return ResponseEntity.ok(productVariant);
    }

    @GetMapping("/sku/{sku}")
    public ResponseEntity<ProductVariantDto> findBySku(@PathVariable String sku) {
        log.debug("REST request to get ProductVariant by SKU: {}", sku);
        ProductVariantDto productVariant = productVariantService.findBySku(sku);
        return ResponseEntity.ok(productVariant);
    }

    @GetMapping("/upc/{upc}")
    public ResponseEntity<ProductVariantDto> findByUpc(@PathVariable String upc) {
        log.debug("REST request to get ProductVariant by UPC: {}", upc);
        ProductVariantDto productVariant = productVariantService.findByUpc(upc);
        return ResponseEntity.ok(productVariant);
    }

    @GetMapping("/family/{familyId}")
    public ResponseEntity<List<ProductVariantDto>> findByFamilyId(@PathVariable UUID familyId) {
        log.debug("REST request to get ProductVariants by family ID: {}", familyId);
        List<ProductVariantDto> productVariants = productVariantService.findByFamilyId(familyId);
        return ResponseEntity.ok(productVariants);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<Page<ProductVariantDto>> findByStatus(
            @PathVariable ProductStatus status,
            Pageable pageable) {
        log.debug("REST request to get ProductVariants by status: {}", status);
        Page<ProductVariantDto> page = productVariantService.findByStatus(status, pageable);
        return ResponseEntity.ok(page);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ProductVariantDto> create(
            @Valid @RequestBody ProductVariantRequestDto productVariantDto,
            @RequestHeader("X-User-Id") String createdBy) {
        log.debug("REST request to create ProductVariant: {}", productVariantDto.getSku());
        ProductVariantDto createdProductVariant = productVariantService.create(productVariantDto, createdBy);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProductVariant);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ProductVariantDto>> search(
            @RequestParam String term,
            Pageable pageable) {
        log.debug("REST request to search ProductVariants by term: {}", term);
        Page<ProductVariantDto> page = productVariantService.searchByTerm(term, pageable);
        return ResponseEntity.ok(page);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductVariantDto> update(
            @PathVariable UUID id,
            @Valid @RequestBody ProductVariantRequestDto productVariantDto){
        log.debug("REST request to update ProductVariant: {}", id);
        ProductVariantDto updatedProductVariant = productVariantService.update(id, productVariantDto, "system");
        return ResponseEntity.ok(updatedProductVariant);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        log.debug("REST request to delete ProductVariant: {}", id);
        productVariantService.delete(id);
    }
}