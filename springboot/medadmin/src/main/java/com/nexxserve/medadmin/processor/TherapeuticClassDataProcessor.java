package com.nexxserve.medadmin.processor;

import com.nexxserve.medadmin.dto.sync.SyncSessionResponse;
import com.nexxserve.medadmin.dto.sync.TherapeuticClassSyncData;
import com.nexxserve.medadmin.service.sync.TherapeuticClassSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Component
@Slf4j
@RequiredArgsConstructor
public class TherapeuticClassDataProcessor extends BaseDataProcessor<TherapeuticClassSyncData> {

    private final TherapeuticClassSyncService therapeuticClassSyncService;

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
        therapeuticClassSyncService.saveTherapeuticClassFromSync(syncData);
    }

    public void processTherapeuticClassesData(SyncSessionResponse response) {
        log.info("Starting therapeutic class data process sync...");
        processData(response);
    }

}