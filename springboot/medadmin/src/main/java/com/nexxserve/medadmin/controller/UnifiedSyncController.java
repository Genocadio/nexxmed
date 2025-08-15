package com.nexxserve.medadmin.controller;

import com.nexxserve.medadmin.dto.sync.*;
import com.nexxserve.medadmin.entity.Insurance;
import com.nexxserve.medadmin.service.InsuranceService;
import com.nexxserve.medadmin.service.sync.out.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/sync")
@RequiredArgsConstructor
@Slf4j
public class UnifiedSyncController {

    @Autowired
    private InsuranceService insuranceService;

    private final TherapeuticClassSyncService therapeuticClassSyncService;
    private final GenericSyncService genericSyncService;
    private final VariantSyncService variantSyncService;
    private final BrandSyncService brandSyncService;
    private final MedicineInsuranceCoverageSyncService medicineInsuranceCoverageSyncService;
    private final CategoryReferenceSyncService categoryReferenceSyncService;
    private final ProductFamilySyncService productFamilySyncService;
    private final ProductVariantSyncService productVariantSyncService;
    private final ProductInsuranceCoverageSyncService productInsuranceCoverageSyncService;

    @GetMapping("/categories")
    public ResponseEntity<Page<CategoryReferenceSyncData>> getCategoriesForSync(
            @RequestParam(required = false) Double lastSyncVersion,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "500") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<CategoryReferenceSyncData> categories;

        if (lastSyncVersion != null) {
            log.info("Fetching categories with sync version greater than {}", lastSyncVersion);
            categories = categoryReferenceSyncService.findBySyncVersionGreaterThan(lastSyncVersion, pageable);
        } else {
            log.info("Fetching all categories");
            categories = categoryReferenceSyncService.findAll(pageable);
        }

        log.info("Returning {} categories for sync", categories.getContent().size());
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/product-families")
    public ResponseEntity<Page<ProductFamilySyncData>> getProductFamiliesForSync(
            @RequestParam(required = false) Double lastSyncVersion,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "500") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ProductFamilySyncData> productFamilies;

        if (lastSyncVersion != null) {
            log.info("Fetching product families with sync version greater than {}", lastSyncVersion);
            productFamilies = productFamilySyncService.findBySyncVersionGreaterThan(lastSyncVersion, pageable);
        } else {
            log.info("Fetching all product families");
            productFamilies = productFamilySyncService.findAll(pageable);
        }

        log.info("Returning {} product families for sync", productFamilies.getContent().size());
        return ResponseEntity.ok(productFamilies);
    }

    @GetMapping("/product-variants")
    public ResponseEntity<Page<ProductVariantSyncData>> getProductVariantsForSync(
            @RequestParam(required = false) Double lastSyncVersion,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "500") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ProductVariantSyncData> productVariants;

        if (lastSyncVersion != null) {
            log.info("Fetching product variants with sync version greater than {}", lastSyncVersion);
            productVariants = productVariantSyncService.findBySyncVersionGreaterThan(lastSyncVersion, pageable);
        } else {
            log.info("Fetching all product variants");
            productVariants = productVariantSyncService.findAll(pageable);
        }

        log.info("Returning {} product variants for sync", productVariants.getContent().size());
        return ResponseEntity.ok(productVariants);
    }

    @GetMapping("/product-insurance-coverages")
    public ResponseEntity<Page<ProductInsuranceCoverageSyncData>> getProductInsuranceCoveragesForSync(
            @RequestParam(required = false) Double lastSyncVersion,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "500") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ProductInsuranceCoverageSyncData> coverages;

        if (lastSyncVersion != null) {
            log.info("Fetching product insurance coverages with sync version greater than {}", lastSyncVersion);
            coverages = productInsuranceCoverageSyncService.findBySyncVersionGreaterThan(lastSyncVersion, pageable);
        } else {
            log.info("Fetching all product insurance coverages");
            coverages = productInsuranceCoverageSyncService.findAll(pageable);
        }

        log.info("Returning {} product insurance coverages for sync", coverages.getContent().size());
        return ResponseEntity.ok(coverages);
    }


    @GetMapping("/brands")
    public ResponseEntity<Page<BrandSyncData>> getBrandsForSync(
            @RequestParam(required = false) Double lastSyncVersion,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "500") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<BrandSyncData> brands;

        if (lastSyncVersion != null) {
            log.info("Fetching brands with sync version greater than {}", lastSyncVersion);
            brands = brandSyncService.findBySyncVersionGreaterThan(lastSyncVersion, pageable);
        } else {
            log.info("Fetching all brands");
            brands = brandSyncService.findAll(pageable);
        }

        log.info("Returning {} brands for sync", brands.getContent().size());
        return ResponseEntity.ok(brands);
    }

    @GetMapping("/variants")
    public ResponseEntity<Page<VariantSyncData>> getVariantsForSync(
            @RequestParam(required = false) Double lastSyncVersion,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "500") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<VariantSyncData> variants;

        if (lastSyncVersion != null) {
            log.info("Fetching variants with sync version greater than {}", lastSyncVersion);
            variants = variantSyncService.findBySyncVersionGreaterThan(lastSyncVersion, pageable);
        } else {
            log.info("Fetching all variants");
            variants = variantSyncService.findAll(pageable);
        }

        log.info("Returning {} variants for sync", variants.getContent().size());
        return ResponseEntity.ok(variants);
    }

    @GetMapping("/medicine-insurance-coverages")
    public ResponseEntity<Page<MedicineInsuranceCoverageSyncData>> getMedicineInsuranceCoverages(
            @RequestParam(required = false) Double lastSyncVersion,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "500") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<MedicineInsuranceCoverageSyncData> coverages;

        if (lastSyncVersion != null) {
            log.info("Fetching medicine insurance coverages with sync version greater than {}", lastSyncVersion);
            coverages = medicineInsuranceCoverageSyncService.findBySyncVersionGreaterThan(lastSyncVersion, pageable);
        } else {
            log.info("Fetching all medicine insurance coverages");
            coverages = medicineInsuranceCoverageSyncService.findAll(pageable);
        }

        log.info("Returning {} medicine insurance coverages for sync", coverages.getContent().size());
        return ResponseEntity.ok(coverages);
    }


    @GetMapping("/classes")
    public ResponseEntity<Page<TherapeuticClassSyncData>> getTherapeuticClassesForSync(
            @RequestParam(required = false) Double lastSyncVersion,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "500") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<TherapeuticClassSyncData> therapeuticClasses;

        if (lastSyncVersion != null) {
            log.info("Fetching therapeutic classes with sync version greater than {}", lastSyncVersion);
            therapeuticClasses = therapeuticClassSyncService.findBySyncVersionGreaterThan(lastSyncVersion, pageable);
        } else {
            log.info("Fetching all therapeutic classes");
            therapeuticClasses = therapeuticClassSyncService.findAll(pageable);
        }

        log.info("Returning {} therapeutic classes for sync", therapeuticClasses.getContent().size());
        return ResponseEntity.ok(therapeuticClasses);
    }

    @GetMapping("/insurances")
    public ResponseEntity<Page<Insurance>> getInsurancesForSync(
            @RequestParam(required = false) Double lastSyncVersion,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "1000") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Insurance> insurances;

        if (lastSyncVersion != null) {
            log.info("Fetching insurances with sync version greater than {}", lastSyncVersion);
            insurances = insuranceService.findBySyncVersionGreaterThan(lastSyncVersion, pageable);
        } else {
            log.info("Fetching all insurances");
            insurances = insuranceService.findAll(pageable);
        }

        return ResponseEntity.ok(insurances);
    }

    /**
     * Sync generics (second level - children of therapeutic classes)
     * Should be called after therapeutic classes are synced
     */
    @GetMapping("/generics")
    public ResponseEntity<Page<GenericSyncData>> getGenericsForSync(
            @RequestParam(required = false) Double lastSyncVersion,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "500") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<GenericSyncData> generics;

        if (lastSyncVersion != null) {
            log.info("Fetching generics with sync version greater than {}", lastSyncVersion);
            generics = genericSyncService.findBySyncVersionGreaterThan(lastSyncVersion, pageable);
        } else {
            log.info("Fetching all generics");
            generics = genericSyncService.findAll(pageable);
        }

        log.info("Returning {} generics for sync", generics.getContent().size());
        return ResponseEntity.ok(generics);
    }


}