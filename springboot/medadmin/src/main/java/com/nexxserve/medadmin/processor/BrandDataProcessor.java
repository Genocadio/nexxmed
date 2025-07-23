package com.nexxserve.medadmin.processor;

import com.nexxserve.medadmin.dto.sync.BrandSyncData;
import com.nexxserve.medadmin.dto.sync.SyncSessionResponse;
import com.nexxserve.medadmin.service.sync.BrandSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Component
@Slf4j
@RequiredArgsConstructor
public class BrandDataProcessor extends BaseDataProcessor<BrandSyncData> {
    private final BrandSyncService brandSyncService;
    @Override
    protected String getExpectedStage() {
        return "BRANDS";
    }
    @Override
    protected String getStageDisplayName() {
        return "BRAND";
    }
    @Override
    protected String getDataTypeName() {
        return "brand";
    }
    @Override
    protected Class<BrandSyncData> getTargetClass() {
        return BrandSyncData.class;
    }
    @Override
    protected void processSingleRecord(BrandSyncData syncData) {
        brandSyncService.saveBrandFromSync(syncData);
    }

    public void processBrandData(SyncSessionResponse response) {
        log.info("Starting brand data process sync...");
        processData(response);
    }
}
