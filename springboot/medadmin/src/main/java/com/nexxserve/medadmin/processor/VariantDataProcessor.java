package com.nexxserve.medadmin.processor;

import com.nexxserve.medadmin.dto.sync.SyncSessionResponse;
import com.nexxserve.medadmin.dto.sync.VariantSyncData;
import com.nexxserve.medadmin.service.sync.in.VariantSyncInService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class VariantDataProcessor extends  BaseDataProcessor<VariantSyncData> {

    private final VariantSyncInService syncService;

    @Override
    protected String getExpectedStage() {
        return "VARIANTS";
    }
    @Override
    protected String getStageDisplayName() {
        return "VARIANT";
    }
    @Override
    protected String getDataTypeName() {
        return "variant";
    }
    @Override
    protected Class<VariantSyncData> getTargetClass() {
        return VariantSyncData.class;
    }
    @Override
    protected void processSingleRecord(VariantSyncData syncData) {
        syncService.saveVariantFromSync(syncData);
    }
    public void processVariantsData(SyncSessionResponse response) {
        log.info("Starting variant data process sync...");
        processData(response);
    }


}
