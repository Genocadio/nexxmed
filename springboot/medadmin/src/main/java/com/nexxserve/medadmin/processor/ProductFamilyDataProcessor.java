package com.nexxserve.medadmin.processor;

import com.nexxserve.medadmin.dto.sync.ProductFamilySyncData;
import com.nexxserve.medadmin.dto.sync.SyncSessionResponse;
import com.nexxserve.medadmin.service.sync.ProductFamilySyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ProductFamilyDataProcessor extends BaseDataProcessor<ProductFamilySyncData> {

    private final ProductFamilySyncService productFamilySyncService;

    @Override
    protected String getExpectedStage() {
        return "PRODUCT_FAMILIES";
    }

    @Override
    protected String getStageDisplayName() {
        return "PRODUCT FAMILY";
    }

    @Override
    protected String getDataTypeName() {
        return "product family";
    }

    @Override
    protected Class<ProductFamilySyncData> getTargetClass() {
        return ProductFamilySyncData.class;
    }

    @Override
    protected void processSingleRecord(ProductFamilySyncData syncData) {
        productFamilySyncService.saveProductFamilyFromSync(syncData);
    }
    public void processProductFamilyData(SyncSessionResponse response) {
        log.info("Starting product family data process sync...");
        processData(response);
    }
}
