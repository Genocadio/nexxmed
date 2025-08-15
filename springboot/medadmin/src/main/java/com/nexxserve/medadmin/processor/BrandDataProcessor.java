package com.nexxserve.medadmin.processor;

import com.nexxserve.medadmin.dto.sync.BrandSyncData;
import com.nexxserve.medadmin.dto.sync.SyncSessionResponse;
import com.nexxserve.medadmin.service.sync.in.BrandSyncInService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Component
@Slf4j
@RequiredArgsConstructor
public class BrandDataProcessor extends BaseDataProcessor<BrandSyncData> {
    private final BrandSyncInService brandSyncInService;
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
        brandSyncInService.saveBrandFromSync(syncData);
    }

    public void processBrandData(SyncSessionResponse response) {
        log.info("Starting brand data process sync...");
        processData(response);
    }
}
