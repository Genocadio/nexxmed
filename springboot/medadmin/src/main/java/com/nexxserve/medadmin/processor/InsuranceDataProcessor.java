package com.nexxserve.medadmin.processor;

import com.nexxserve.medadmin.dto.sync.InsuranceDto;
import com.nexxserve.medadmin.dto.sync.SyncSessionResponse;
import com.nexxserve.medadmin.service.sync.in.InsuranceSyncInService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class InsuranceDataProcessor extends BaseDataProcessor<InsuranceDto> {


    private final InsuranceSyncInService insuranceSyncInService;

    @Override
    protected String getExpectedStage() {
        return "INSURANCES";
    }
    @Override
    protected String getStageDisplayName() {
        return "INSURANCE";
    }
    @Override
    protected String getDataTypeName() {
        return "insurance";
    }
    @Override
    protected Class<InsuranceDto> getTargetClass() {
        return InsuranceDto.class;
    }

    @Override
    protected void processSingleRecord(InsuranceDto syncData) {
        insuranceSyncInService.saveInsuranceFromSync(syncData);
    }
    public void processInsurancesData(SyncSessionResponse response) {
        log.info("Starting insurance data process sync...");
        processData(response);
    }


}