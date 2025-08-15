package com.nexxserve.inventoryservice.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexxserve.inventoryservice.dto.sync.SyncSessionResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public abstract class BaseDataProcessor<T> {

    @Autowired
    protected ObjectMapper objectMapper;

    /**
     * Template method that handles the common processing flow
     */
    public void processData(SyncSessionResponse response) {
        String expectedStage = getExpectedStage();

        if (!expectedStage.equals(response.getStage())) {
            log.warn("Expected {} stage but got {}", expectedStage, response.getStage());
            return;
        }

        log.info("=== {} PROCESSING === for session ID {}",
                getStageDisplayName(), response.getSessionId());

        List<T> syncDataList = convertToSyncData(response.getData());

        log.info("Stage: {} (Page {}/{})", response.getStage(),
                response.getPage() + 1, response.getTotalPages());
        log.info("Processing {} {} records out of {} total",
                syncDataList.size(), getDataTypeName(), response.getTotalElements());

        // Process each record using the specific implementation
        for (int i = 0; i < syncDataList.size(); i++) {
            T syncData = syncDataList.get(i);
            processSingleRecord(syncData);
        }

        // Log progress if implemented
        logProgress(response, syncDataList.size());

        if (response.isStageCompleted()) {
            log.info("✅ {} STAGE COMPLETED - Moving to next stage", getStageDisplayName());
            log.info("Total {} records processed: {}", getDataTypeName(), response.getTotalElements());
        }
    }

    /**
     * Convert raw data to typed sync data objects
     */
    @SuppressWarnings("unchecked")
    private List<T> convertToSyncData(List<?> data) {
        try {
            List<LinkedHashMap<String, Object>> rawData = (List<LinkedHashMap<String, Object>>) data;

            return rawData.stream()
                    .map(this::convertMapToSyncData)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Error converting data to {} objects: {}", getTargetClass().getSimpleName(), e.getMessage(), e);
            throw new RuntimeException("Failed to convert " + getDataTypeName() + " data", e);
        }
    }

    /**
     * Convert LinkedHashMap to the specific sync data type
     */
    private T convertMapToSyncData(LinkedHashMap<String, Object> map) {
        try {
            return objectMapper.convertValue(map, getTargetClass());
        } catch (Exception e) {
            log.error("Error converting map to {}: {}", getTargetClass().getSimpleName(), e.getMessage());
            log.debug("Problematic map data: {}", map);
            throw new RuntimeException("Failed to convert " + getDataTypeName() + " map to DTO", e);
        }
    }

    /**
     * Default progress logging - can be overridden for custom progress logging
     */
    protected void logProgress(SyncSessionResponse response, int processedCount) {
        double progressPercentage = ((double) (response.getPage() + 1) / response.getTotalPages()) * 100;

        log.info("📊 {} Progress: {:.1f}% ({}/{} pages)",
                getStageDisplayName(), progressPercentage, response.getPage() + 1, response.getTotalPages());
        log.info("Records in this batch: {}", processedCount);
        log.info("Has more pages: {}", response.isHasMorePages());

        if (response.getMessage() != null) {
            log.info("Status: {}", response.getMessage());
        }

        // Log detailed progress if available
        if (response.getProgress() != null) {
            var progress = response.getProgress();
            log.info("Overall sync progress: Stage {}/{} - {} ({}% complete)",
                    progress.getCurrentStage(), progress.getTotalStages(),
                    progress.getStageName(), progress.getCompletionPercentage());
            log.info("Records processed in current stage: {}", progress.getRecordsProcessed());
        }
    }

    // Abstract methods that must be implemented by concrete classes

    /**
     * Returns the expected stage name for validation
     */
    protected abstract String getExpectedStage();

    /**
     * Returns the display name for logging purposes
     */
    protected abstract String getStageDisplayName();

    /**
     * Returns the data type name for logging purposes
     */
    protected abstract String getDataTypeName();

    /**
     * Returns the target class for ObjectMapper conversion
     */
    protected abstract Class<T> getTargetClass();

    /**
     * Processes a single record - implement the specific business logic here
     */
    protected abstract void processSingleRecord(T syncData);
}