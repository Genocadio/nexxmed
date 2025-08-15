package com.nexxserve.medadmin.processor;

import com.nexxserve.medadmin.dto.sync.ProductVariantSyncData;
import com.nexxserve.medadmin.dto.sync.SyncSessionResponse;
import com.nexxserve.medadmin.service.sync.in.ProductVariantSyncInService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductVariantDataProcessor extends  BaseDataProcessor<ProductVariantSyncData> {
    private final ProductVariantSyncInService productVariantSyncInService;
    @Override
    protected String getExpectedStage() {
        return "PRODUCT_VARIANTS";
    }

    @Override
    protected String getStageDisplayName() {
        return "PRODUCT VARIANT";
    }

    @Override
    protected String getDataTypeName() {
        return "product variant";
    }

    @Override
    protected Class<ProductVariantSyncData> getTargetClass() {
        return ProductVariantSyncData.class;
    }

    @Override
    protected void processSingleRecord(ProductVariantSyncData syncData) {
        productVariantSyncInService.saveProductVariantFromSync(syncData);
    }
    public void processProductVariantData(SyncSessionResponse response) {
        log.info("Starting product variant data process sync...");
        processData(response);
    }
}
