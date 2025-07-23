package com.nexxserve.inventoryservice.controller;

import com.nexxserve.inventoryservice.dto.catalog.ProductVariantDto;
import com.nexxserve.inventoryservice.dto.catalog.ProductVariantRequestDto;
import com.nexxserve.inventoryservice.service.ProductVariantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/product-variants")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ProductVariantController {

    private final ProductVariantService productVariantService;

    // src/main/java/com/nexxserve/inventoryservice/controller/ProductVariantController.java
    @GetMapping
    public ResponseEntity<Page<ProductVariantDto>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        log.debug("REST request to get page {} of ProductVariants", page);

        Sort sort = sortDir.equalsIgnoreCase("asc") ?
                Sort.by(sortBy).ascending() :
                Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ProductVariantDto> variants = productVariantService.getAllProductVariantsPaginated(pageable);

        return ResponseEntity.ok(variants);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductVariantDto> findById(@PathVariable UUID id) {
        log.debug("REST request to get ProductVariant: {}", id);
        ProductVariantDto productVariant = productVariantService.getProductVariantById(id);
        return ResponseEntity.ok(productVariant);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ProductVariantDto> create(
            @Valid @RequestBody ProductVariantRequestDto productVariantDto) {
        log.debug("REST request to create ProductVariant: {}", productVariantDto.getSku());
        ProductVariantDto createdProductVariant = productVariantService.createProductVariant(productVariantDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProductVariant);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductVariantDto> update(
            @PathVariable UUID id,
            @Valid @RequestBody ProductVariantRequestDto productVariantDto){
        log.debug("REST request to update ProductVariant: {}", id);
        ProductVariantDto updatedProductVariant = productVariantService.updateProductVariant(id, productVariantDto);
        return ResponseEntity.ok(updatedProductVariant);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        log.debug("REST request to delete ProductVariant: {}", id);
        productVariantService.deleteProductVariant(id);
    }
}