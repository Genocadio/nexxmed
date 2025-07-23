package com.nexxserve.medadmin.processor;

import com.nexxserve.medadmin.dto.sync.ProductInsuranceCoverageSyncData;
import com.nexxserve.medadmin.dto.sync.SyncSessionResponse;
import com.nexxserve.medadmin.service.sync.ProductInsuranceCoverageSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ProductInsuranceDataProcessor extends BaseDataProcessor<ProductInsuranceCoverageSyncData> {
    private final ProductInsuranceCoverageSyncService productInsuranceSyncService;

    @Override
    protected String getExpectedStage() {
        return "PRODUCT_INSURANCE_COVERAGES";
    }

    @Override
    protected String getStageDisplayName() {
        return "PRODUCT INSURANCE COVERAGE";
    }

    @Override
    protected String getDataTypeName() {
        return "product insurance coverage";
    }

    @Override
    protected Class<ProductInsuranceCoverageSyncData> getTargetClass() {
        return ProductInsuranceCoverageSyncData.class;
    }
    @Override
    protected void processSingleRecord(ProductInsuranceCoverageSyncData syncData) {
        productInsuranceSyncService.saveProductInsuranceCoverageFromSync(syncData);
    }
    public void processProductInsuranceData(SyncSessionResponse response) {
        log.info("Starting product insurance data process sync...");
        processData(response);
    }
}
