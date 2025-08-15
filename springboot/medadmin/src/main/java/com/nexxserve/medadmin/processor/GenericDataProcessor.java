package com.nexxserve.medadmin.processor;

import com.nexxserve.medadmin.dto.sync.GenericSyncData;
import com.nexxserve.medadmin.dto.sync.SyncSessionResponse;
import com.nexxserve.medadmin.service.sync.in.GenericSyncInService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Component
@Slf4j
@RequiredArgsConstructor
public class GenericDataProcessor extends BaseDataProcessor<GenericSyncData> {

    private final GenericSyncInService genericSyncInService;

    @Override
    protected String getExpectedStage() {
        return "GENERICS";
    }

    @Override
    protected String getStageDisplayName() {
        return "GENERIC";
    }

    @Override
    protected String getDataTypeName() {
        return "generic";
    }

    @Override
    protected Class<GenericSyncData> getTargetClass() {
        return GenericSyncData.class;
    }

    @Override
    protected void processSingleRecord(GenericSyncData syncData) {
        genericSyncInService.saveGenericFromSync(syncData);
    }

    public void processGenercisData(SyncSessionResponse response) {
        log.info("Starting generic data process sync...");
        processData(response);
    }
}