package com.nexxserve.medadmin.controller;


import com.nexxserve.medadmin.service.sync.*;
import com.nexxserve.medadmin.service.sync.in.SyncClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/sync")
@RequiredArgsConstructor
@Slf4j
public class SyncController {

    private final InsuranceSyncService insuranceSyncService;
    private final TherapeuticClassSyncService therapeuticClassSyncService;
    private final BrandSyncService brandSyncService;
    private final VariantSyncService variantSyncService;
    private final GenericSyncService genericSyncService;
    private final MedicineInsuranceCoverageSyncService medicineInsuranceCoverageSyncService;
    private final CategoryReferenceSyncService categoryReferenceSyncService;
    private final ProductFamilySyncService productFamilySyncService;
    private final ProductVariantSyncService productVariantSyncService;
    private final ProductInsuranceCoverageSyncService productInsuranceCoverageSyncService;
    private final SyncClient syncService;

    /**
     * Manual sync endpoints for various entities
     */
    @PostMapping("/sync")
    public ResponseEntity<String> manualSync() {
        try {
            log.info("Starting manual sync");
            String deviceId = "manual-sync"; // Use a fixed device ID for manual sync
            syncService.performCompleteSync(deviceId);
            return ResponseEntity.ok("Manual sync completed successfully");
        } catch (Exception e) {
            log.error("Manual sync failed", e);
            return ResponseEntity.internalServerError()
                    .body("Manual sync failed: " + e.getMessage());
        }
    }

    @PostMapping("/product-coverage")
    public ResponseEntity<String> syncProductInsuranceCoverages() {
        try {
            log.info("Starting manual product insurance coverage sync");
            productInsuranceCoverageSyncService.syncProductInsuranceCoverages();
            return ResponseEntity.ok("Product insurance coverage sync completed successfully");
        } catch (Exception e) {
            log.error("Manual sync failed", e);
            return ResponseEntity.internalServerError()
                    .body("Product insurance coverage sync failed: " + e.getMessage());
        }
    }

    @PostMapping("/product-variants")
    public ResponseEntity<String> syncProductVariants() {
        try {
            log.info("Starting manual product variant sync");
            productVariantSyncService.syncProductVariants();
            return ResponseEntity.ok("Product variant sync completed successfully");
        } catch (Exception e) {
            log.error("Manual sync failed", e);
            return ResponseEntity.internalServerError()
                    .body("Product variant sync failed: " + e.getMessage());
        }
    }

    @PostMapping("/product-families")
    public ResponseEntity<String> syncProductFamilies() {
        try {
            log.info("Starting manual product family sync");
            productFamilySyncService.syncProductFamilies();
            return ResponseEntity.ok("Product family sync completed successfully");
        } catch (Exception e) {
            log.error("Manual sync failed", e);
            return ResponseEntity.internalServerError()
                    .body("Product family sync failed: " + e.getMessage());
        }
    }

    @PostMapping("/category-references")
    public ResponseEntity<String> syncCategoryReferences() {
        try {
            log.info("Starting manual category reference sync");
            categoryReferenceSyncService.syncCategoryReferences();
            return ResponseEntity.ok("Category reference sync completed successfully");
        } catch (Exception e) {
            log.error("Manual sync failed", e);
            return ResponseEntity.internalServerError()
                    .body("Category reference sync failed: " + e.getMessage());
        }
    }


    @PostMapping("/medicine-coverage")
    public ResponseEntity<String> syncMedicineInsuranceCoverage() {
        try {
            log.info("Starting manual medicine insurance coverage sync");
            medicineInsuranceCoverageSyncService.syncMedicineInsuranceCoverages();
            return ResponseEntity.ok("Medicine insurance coverage sync completed successfully");
        } catch (Exception e) {
            log.error("Manual sync failed", e);
            return ResponseEntity.internalServerError()
                    .body("Medicine insurance coverage sync failed: " + e.getMessage());
        }
    }

    @PostMapping("/insurances")
    public ResponseEntity<String> syncInsurances() {
        try {
            insuranceSyncService.syncInsurances();
            return ResponseEntity.ok("Insurance sync completed successfully");
        } catch (Exception e) {
            log.error("Manual sync failed", e);
            return ResponseEntity.internalServerError()
                    .body("Insurance sync failed: " + e.getMessage());
        }
    }

    @PostMapping("/classes")
    public ResponseEntity<String> syncTherapeuticClasses() {
        try {
            log.info("Starting manual therapeutic class sync");
            therapeuticClassSyncService.syncTherapeuticClasses();
            return ResponseEntity.ok("Therapeutic class sync completed successfully");
        } catch (Exception e) {
            log.error("Manual sync failed", e);
            return ResponseEntity.internalServerError()
                    .body("Therapeutic class sync failed: " + e.getMessage());
        }
    }

    @PostMapping("variants")
    public ResponseEntity<String> syncVariants() {
        try {
            log.info("Starting manual variant sync");
            variantSyncService.syncVariants();
            return ResponseEntity.ok("Variant sync completed successfully");
        } catch (Exception e) {
            log.error("Manual sync failed", e);
            return ResponseEntity.internalServerError()
                    .body("Variant sync failed: " + e.getMessage());
        }
    }

    @PostMapping("generics")
    public ResponseEntity<String> syncGenerics() {
        try {
            log.info("Starting manual generic sync");
            genericSyncService.syncGenerics();
            return ResponseEntity.ok("General sync completed successfully");
        } catch (Exception e) {
            log.error("Manual sync failed", e);
            return ResponseEntity.internalServerError()
                    .body("General sync failed: " + e.getMessage());
        }
    }

    @PostMapping("brands")
    public ResponseEntity<String> syncBrands() {
        try {
            log.info("Starting manual brand sync");
            brandSyncService.syncBrands();
            return ResponseEntity.ok("Brand sync completed successfully");
        } catch (Exception e) {
            log.error("Manual sync failed", e);
            return ResponseEntity.internalServerError()
                    .body("Brand sync failed: " + e.getMessage());
        }
    }


}