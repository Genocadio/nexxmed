package com.nexxserve.inventoryservice.processor;

import com.nexxserve.inventoryservice.dto.sync.ProductFamilySyncData;
import com.nexxserve.inventoryservice.dto.sync.SyncSessionResponse;
import com.nexxserve.inventoryservice.service.sync.in.ProductFamilySyncInService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ProductFamilyDataProcessor extends BaseDataProcessor<ProductFamilySyncData> {

    private final ProductFamilySyncInService productFamilySyncInService;

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
        productFamilySyncInService.saveProductFamilyFromSync(syncData);
    }
    public void processProductFamilyData(SyncSessionResponse response) {
        log.info("Starting product family data process sync...");
        processData(response);
    }
}
