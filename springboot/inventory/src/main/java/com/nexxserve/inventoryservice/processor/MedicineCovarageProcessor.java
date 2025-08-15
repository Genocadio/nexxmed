package com.nexxserve.inventoryservice.processor;

import com.nexxserve.inventoryservice.dto.sync.MedicineInsuranceCoverageSyncData;
import com.nexxserve.inventoryservice.dto.sync.SyncSessionResponse;
import com.nexxserve.inventoryservice.service.sync.in.MedicineInsuranceCoverageSyncInService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class MedicineCovarageProcessor extends BaseDataProcessor<MedicineInsuranceCoverageSyncData> {
    private final MedicineInsuranceCoverageSyncInService syncService;

    @Override
    protected String getExpectedStage() {
        return "MEDICINE_COVERAGES";
    }
    @Override
    protected String getStageDisplayName() {
        return "MEDICINE COVERAGE";
    }
    @Override
    protected String getDataTypeName() {
        return "medicine coverage";
    }
    @Override
    protected Class<MedicineInsuranceCoverageSyncData> getTargetClass() {
        return MedicineInsuranceCoverageSyncData.class;
    }

    @Override
    protected void processSingleRecord(MedicineInsuranceCoverageSyncData syncData) {
        syncService.saveMedicineInsuranceCoverageFromSync(syncData);
    }

    public void processMedicineCoverageData(SyncSessionResponse response) {
        log.info("Starting generic data process sync...");
        processData(response);
    }

}
