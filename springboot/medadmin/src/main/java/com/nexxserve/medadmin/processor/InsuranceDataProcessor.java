package com.nexxserve.medadmin.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexxserve.medadmin.dto.sync.InsuranceDto;
import com.nexxserve.medadmin.dto.sync.InsuranceSyncData;
import com.nexxserve.medadmin.dto.sync.SyncSessionResponse;
import com.nexxserve.medadmin.service.sync.InsuranceSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class InsuranceDataProcessor extends BaseDataProcessor<InsuranceDto> {


    private final InsuranceSyncService insuranceSyncService;

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
        insuranceSyncService.saveInsuranceFromSync(syncData);
    }
    public void processInsurancesData(SyncSessionResponse response) {
        log.info("Starting insurance data process sync...");
        processData(response);
    }


}