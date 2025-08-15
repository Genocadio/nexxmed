package com.nexxserve.inventoryservice.service.sync.in;

        import com.nexxserve.inventoryservice.dto.sync.SyncSessionResponse;
        import com.nexxserve.inventoryservice.entity.sync.SyncSession;
        import com.nexxserve.inventoryservice.processor.*;
        import com.nexxserve.inventoryservice.security.ClientCredentialsStore;
        import lombok.RequiredArgsConstructor;
        import lombok.extern.slf4j.Slf4j;
        import org.springframework.http.HttpEntity;
        import org.springframework.http.HttpHeaders;
        import org.springframework.http.HttpMethod;
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
            private final ClientCredentialsStore clientCredentialsStore;
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

            public String getBaseUrl() {
                String baseUrl = clientCredentialsStore.getServerUrl();
                if (baseUrl == null || baseUrl.isEmpty()) {
                    log.error("Base URL not set in ClientCredentialsStore");
                    throw new IllegalStateException("Base URL not set in ClientCredentialsStore");
                }
                return baseUrl + "/api/session";
            }

            private HttpHeaders createAuthHeaders() {
                HttpHeaders headers = new HttpHeaders();
                String token = clientCredentialsStore.getToken();
                if (token != null && !token.isEmpty()) {
                    headers.setBearerAuth(token);
                } else {
                    log.warn("No token available for authentication");
                }
                return headers;
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

            // REST API helper methods with bearer token authentication

            private SyncSession startSyncSession(String deviceId, Double lastSyncVersion) {
                String url = getBaseUrl() + "/start?deviceId=" + deviceId;
                if (lastSyncVersion != null) {
                    url += "&lastSyncVersion=" + lastSyncVersion;
                }

                try {
                    HttpEntity<Void> entity = new HttpEntity<>(createAuthHeaders());
                    ResponseEntity<SyncSession> response = restTemplate.exchange(url, HttpMethod.POST, entity, SyncSession.class);
                    return response.getBody();
                } catch (Exception e) {
                    log.error("Failed to start sync session", e);
                    return null;
                }
            }

            private SyncSession resumeSyncSession(String sessionId) {
                String url = getBaseUrl() + "/resume/" + sessionId;

                try {
                    HttpEntity<Void> entity = new HttpEntity<>(createAuthHeaders());
                    ResponseEntity<SyncSession> response = restTemplate.exchange(url, HttpMethod.POST, entity, SyncSession.class);
                    return response.getBody();
                } catch (Exception e) {
                    log.error("Failed to resume sync session", e);
                    return null;
                }
            }

            private SyncSessionResponse getNextSyncData(String sessionId, int pageSize) {
                String url = getBaseUrl() + "/next/" + sessionId + "?pageSize=" + pageSize;

                try {
                    HttpEntity<Void> entity = new HttpEntity<>(createAuthHeaders());
                    ResponseEntity<SyncSessionResponse> response = restTemplate.exchange(url, HttpMethod.GET, entity, SyncSessionResponse.class);
                    return response.getBody();
                } catch (Exception e) {
                    log.error("Failed to get next sync data", e);
                    return null;
                }
            }

            private List<SyncSession> getActiveSessionsForDevice(String deviceId) {
                String url = getBaseUrl() + "/device/" + deviceId + "/active";

                try {
                    HttpEntity<Void> entity = new HttpEntity<>(createAuthHeaders());
                    ResponseEntity<List> response = restTemplate.exchange(url, HttpMethod.GET, entity, List.class);
                    return response.getBody();
                } catch (Exception e) {
                    log.error("Failed to get active sessions for device", e);
                    return List.of();
                }
            }

            public void cancelSyncSession(String sessionId) {
                String url = getBaseUrl() + "/cancel/" + sessionId;

                try {
                    HttpEntity<Void> entity = new HttpEntity<>(createAuthHeaders());
                    restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
                    log.info("Cancelled sync session: {}", sessionId);
                } catch (Exception e) {
                    log.error("Failed to cancel sync session", e);
                }
            }
        }