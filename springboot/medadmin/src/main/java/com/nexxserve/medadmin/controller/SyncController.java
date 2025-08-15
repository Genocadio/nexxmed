package com.nexxserve.medadmin.controller;


import com.nexxserve.medadmin.service.sync.in.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/sync")
@RequiredArgsConstructor
@Slf4j
public class SyncController {

    private final InsuranceSyncInService insuranceSyncInService;
    private final TherapeuticClassSyncInService therapeuticClassSyncInService;
    private final BrandSyncInService brandSyncInService;
    private final VariantSyncInService variantSyncInService;
    private final GenericSyncInService genericSyncInService;
    private final MedicineInsuranceCoverageSyncInService medicineInsuranceCoverageSyncInService;
    private final CategoryReferenceSyncInService categoryReferenceSyncInService;
    private final ProductFamilySyncInService productFamilySyncInService;
    private final ProductVariantSyncInService productVariantSyncInService;
    private final ProductInsuranceCoverageSyncInService productInsuranceCoverageSyncInService;
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




}