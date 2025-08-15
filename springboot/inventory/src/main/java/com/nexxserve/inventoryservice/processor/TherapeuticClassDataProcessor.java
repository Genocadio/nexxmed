package com.nexxserve.inventoryservice.processor;

import com.nexxserve.inventoryservice.dto.sync.SyncSessionResponse;
import com.nexxserve.inventoryservice.dto.sync.TherapeuticClassSyncData;
import com.nexxserve.inventoryservice.service.sync.in.TherapeuticClassSyncInService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Component
@Slf4j
@RequiredArgsConstructor
public class TherapeuticClassDataProcessor extends BaseDataProcessor<TherapeuticClassSyncData> {

    private final TherapeuticClassSyncInService therapeuticClassSyncInService;

    @Override
    protected String getExpectedStage() {
        return "THERAPEUTIC_CLASSES";
    }

    @Override
    protected String getStageDisplayName() {
        return "THERAPEUTIC CLASS";
    }

    @Override
    protected String getDataTypeName() {
        return "therapeutic class";
    }

    @Override
    protected Class<TherapeuticClassSyncData> getTargetClass() {
        return TherapeuticClassSyncData.class;
    }

    @Override
    protected void processSingleRecord(TherapeuticClassSyncData syncData) {
        therapeuticClassSyncInService.saveTherapeuticClassFromSync(syncData);
    }

    public void processTherapeuticClassesData(SyncSessionResponse response) {
        log.info("Starting therapeutic class data process sync...");
        processData(response);
    }

}