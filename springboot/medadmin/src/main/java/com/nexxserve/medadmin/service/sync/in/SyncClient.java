package com.nexxserve.medadmin.service.sync.in;

import com.nexxserve.medadmin.dto.sync.SyncSessionResponse;
import com.nexxserve.medadmin.entity.sync.SyncSession;
import com.nexxserve.medadmin.processor.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Example client demonstrating how to use the sequential sync service
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SyncClient {

    private final RestTemplate restTemplate;
    private static final String BASE_URL = "http://localhost:5007/inventory/api/session";
    private final InsuranceDataProcessor insuranceDataProcessor;
    private final TherapeuticClassDataProcessor therapeuticClassDataProcessor;
    private final GenericDataProcessor genericDataProcessor;
    private final BrandDataProcessor brandDataProcessor;
    private final VariantDataProcessor variantDataProcessor;
    private final MedicineCovarageProcessor medicineCovarageProcessor;
    private final ProductFamilyDataProcessor productFamilyDataProcessor;
    private final CategoryRefDataProcessor categoryRefDataProcessor;
    private final ProductVariantDataProcessor productVariantDataProcessor;
    private final ProductInsuranceDataProcessor productInsuranceDataProcessor;

    public boolean performCompleteSync(String deviceId) {
        return performCompleteSync(deviceId, null);
    }

    /**
     * Perform a complete sync for a device
     */
    public boolean performCompleteSync(String deviceId, Double lastSyncVersion) {
        log.info("Starting complete sync for device: {}", deviceId);
        try {
            List<SyncSession> activeSessions = getActiveSessionsForDevice(deviceId);
            if (!activeSessions.isEmpty()) {
                log.warn("Device {} already has active session: {}",
                        deviceId, activeSessions.get(0).getSessionId());
                return false;
            }
            SyncSession session = startSyncSession(deviceId, lastSyncVersion);
            if (session == null) {
                log.error("Failed to start sync session for device: {}", deviceId);
                return false;
            }

            log.info("Started sync session {} for device {}", session.getSessionId(), deviceId);
            return processSyncSession(session.getSessionId());

        } catch (Exception e) {
            log.error("Complete sync failed for device: {}", deviceId, e);
            return false;
        }
    }

    /**
     * Resume an interrupted sync session
     */
    public boolean resumeSync(String sessionId) {
        log.info("Resuming sync session: {}", sessionId);

        try {
            SyncSession session = resumeSyncSession(sessionId);
            if (session == null) {
                log.error("Failed to resume sync session: {}", sessionId);
                return false;
            }
            return processSyncSession(sessionId);

        } catch (Exception e) {
            log.error("Failed to resume sync session: {}", sessionId, e);
            return false;
        }
    }

    /**
     * Process a sync session until completion
     */
    private boolean processSyncSession(String sessionId) {
        boolean completed = false;
        int consecutiveErrors = 0;
        final int maxConsecutiveErrors = 3;

        while (!completed && consecutiveErrors < maxConsecutiveErrors) {
            try {
                SyncSessionResponse response = getNextSyncData(sessionId, 500);

                if (response == null) {
                    log.error("Null response received for session: {}", sessionId);
                    consecutiveErrors++;
                    continue;
                }

                // Log progress
                log.info("Session {}: {} - Page {}/{} ({} records)",
                        sessionId, response.getStage(),
                        response.getPage() + 1, response.getTotalPages(),
                        response.getData() != null ? response.getData().size() : 0);
                if (response.getData() != null && !response.getData().isEmpty()) {
                    processDataBatch(response.getStage(), response);
                } else {
                    log.warn("No data to process for session: {}", sessionId);
                }

                completed = response.isCompleted();
                consecutiveErrors = 0;
                if (!completed) {
                    Thread.sleep(100);
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("Sync interrupted for session: {}", sessionId);
                return false;
            } catch (Exception e) {
                log.error("Error processing sync session: {}", sessionId, e);
                consecutiveErrors++;

                // Exponential backoff on errors
                try {
                    TimeUnit.SECONDS.sleep(Math.min(10, consecutiveErrors * 2));
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    return false;
                }
            }
        }

        if (completed) {
            log.info("Sync session {} completed successfully", sessionId);
            return true;
        } else {
            log.error("Sync session {} failed after {} consecutive errors",
                    sessionId, consecutiveErrors);
            return false;
        }
    }

    /**
     * Process a batch of sync data
     */
    private void processDataBatch(String stage, SyncSessionResponse response) {
        switch (stage) {
            case "INSURANCES":
                insuranceDataProcessor.processInsurancesData(response);

                break;
            case "THERAPEUTIC_CLASSES":
                therapeuticClassDataProcessor.processTherapeuticClassesData(response);
                break;
            case "GENERICS":
                genericDataProcessor.processGenercisData(response);
                break;
            case "VARIANTS":
                variantDataProcessor.processVariantsData(response);
                break;
            case "BRANDS":
                brandDataProcessor.processBrandData(response);
                break;
            case "MEDICINE_COVERAGES":
                medicineCovarageProcessor.processMedicineCoverageData(response);
                break;
            case "CATEGORY_REFERENCES":
                categoryRefDataProcessor.processCategoryReferencesData(response);
                break;
            case "PRODUCT_FAMILIES":
                productFamilyDataProcessor.processProductFamilyData(response);
                break;
            case "PRODUCT_VARIANTS":
                productVariantDataProcessor.processProductVariantData(response);
                break;
            case "PRODUCT_INSURANCE_COVERAGES":
                productInsuranceDataProcessor.processProductInsuranceData(response);
                break;
            default:
                log.warn("Unknown stage: {}", stage);
        }
    }

    // REST API helper methods

    private SyncSession startSyncSession(String deviceId, Double lastSyncVersion) {
        String url = BASE_URL + "/start?deviceId=" + deviceId;
        if (lastSyncVersion != null) {
            url += "&lastSyncVersion=" + lastSyncVersion;
        }

        try {
            ResponseEntity<SyncSession> response = restTemplate.postForEntity(url, null, SyncSession.class);
            return response.getBody();
        } catch (Exception e) {
            log.error("Failed to start sync session", e);
            return null;
        }
    }

    private SyncSession resumeSyncSession(String sessionId) {
        String url = BASE_URL + "/resume/" + sessionId;

        try {
            ResponseEntity<SyncSession> response = restTemplate.postForEntity(url, null, SyncSession.class);
            return response.getBody();
        } catch (Exception e) {
            log.error("Failed to resume sync session", e);
            return null;
        }
    }

    private SyncSessionResponse getNextSyncData(String sessionId, int pageSize) {
        String url = BASE_URL + "/next/" + sessionId + "?pageSize=" + pageSize;

        try {
            ResponseEntity<SyncSessionResponse> response = restTemplate.getForEntity(url, SyncSessionResponse.class);
            return response.getBody();
        } catch (Exception e) {
            log.error("Failed to get next sync data", e);
            return null;
        }
    }

    private List<SyncSession> getActiveSessionsForDevice(String deviceId) {
        String url = BASE_URL + "/device/" + deviceId + "/active";

        try {
            ResponseEntity<List> response = restTemplate.getForEntity(url, List.class);
            return response.getBody();
        } catch (Exception e) {
            log.error("Failed to get active sessions for device", e);
            return List.of();
        }
    }

    public void cancelSyncSession(String sessionId) {
        String url = BASE_URL + "/cancel/" + sessionId;

        try {
            restTemplate.postForEntity(url, null, String.class);
            log.info("Cancelled sync session: {}", sessionId);
        } catch (Exception e) {
            log.error("Failed to cancel sync session", e);
        }
    }
}