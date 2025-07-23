package com.nexxserve.medadmin.processor;

import com.nexxserve.medadmin.dto.sync.MedicineInsuranceCoverageSyncData;
import com.nexxserve.medadmin.dto.sync.SyncSessionResponse;
import com.nexxserve.medadmin.service.sync.MedicineInsuranceCoverageSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class MedicineCovarageProcessor extends BaseDataProcessor<MedicineInsuranceCoverageSyncData> {
    private final MedicineInsuranceCoverageSyncService syncService;

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
