package com.nexxserve.medadmin.processor;

import com.nexxserve.medadmin.dto.sync.CategoryReferenceSyncData;
import com.nexxserve.medadmin.dto.sync.SyncSessionResponse;
import com.nexxserve.medadmin.service.sync.in.CategoryReferenceSyncInService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class CategoryRefDataProcessor extends BaseDataProcessor<CategoryReferenceSyncData> {

    private final CategoryReferenceSyncInService service;

    @Override
    protected String getExpectedStage() {
        return "CATEGORY_REFERENCES";
    }

    @Override
    protected String getStageDisplayName() {
        return "CATEGORY REFERENCE";
    }

    @Override
    protected String getDataTypeName() {
        return "category reference";
    }

    @Override
    protected Class<CategoryReferenceSyncData> getTargetClass() {
        return CategoryReferenceSyncData.class;
    }

    @Override
    protected void processSingleRecord(CategoryReferenceSyncData syncData) {
        service.saveCategoryReferenceFromSync(syncData);
    }
    public void processCategoryReferencesData(SyncSessionResponse response) {
        log.info("Starting category reference data process sync...");
        processData(response);
    }
}
