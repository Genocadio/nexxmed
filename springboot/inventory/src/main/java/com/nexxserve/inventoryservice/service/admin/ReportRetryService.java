package com.nexxserve.inventoryservice.service.admin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexxserve.inventoryservice.dto.admin.InventoryReportDTO;
import com.nexxserve.inventoryservice.dto.admin.UserReportDTO;
import com.nexxserve.inventoryservice.entity.ClientCredentialEntity;
import com.nexxserve.inventoryservice.entity.admin.FailedReport;
import com.nexxserve.inventoryservice.enums.ReportStatus;
import com.nexxserve.inventoryservice.enums.ReportType;
import com.nexxserve.inventoryservice.event.ReportFailureEvent;
import com.nexxserve.inventoryservice.repository.ClientCredentialRepository;
import com.nexxserve.inventoryservice.repository.FailedReportRepository;
import com.nexxserve.inventoryservice.security.ClientCredentialsStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportRetryService {

    private final FailedReportRepository failedReportRepository;
    private final InternetConnectivityService connectivityService;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;
    private final ApplicationEventPublisher eventPublisher;
    private final ClientCredentialRepository credentialRepository;
    private final ClientCredentialsStore credentialsStore;

    @EventListener
    @Transactional
    public void handleReportFailure(ReportFailureEvent event) {
        try {
            if (event.getReportType() == ReportType.INVENTORY) {
                @SuppressWarnings("unchecked")
                List<InventoryReportDTO> reports = (List<InventoryReportDTO>) event.getReports();
                saveFailedInventoryReport(reports, event.getError());
            } else if (event.getReportType() == ReportType.USER) {
                @SuppressWarnings("unchecked")
                List<UserReportDTO> reports = (List<UserReportDTO>) event.getReports();
                saveFailedUserReport(reports, event.getError());
            }
        } catch (Exception e) {
            log.error("Failed to handle report failure event", e);
        }
    }

    @PostConstruct
    public void resumeFailedReports() {
        log.info("Resuming failed report processing after service restart");
        processFailedReports();
    }

    @Scheduled(fixedDelayString = "${app.report.retry.interval:60000}") // Default 1 minute
    public void processFailedReports() {
        try {
            // Check internet connectivity first
            connectivityService.checkInternetConnectivity();

            if (!connectivityService.isInternetAvailable()) {
                log.debug("No internet connectivity - skipping failed report processing");
                return;
            }

            List<FailedReport> reportsToRetry = failedReportRepository.findReportsReadyForRetry(LocalDateTime.now());

            if (!reportsToRetry.isEmpty()) {
                log.info("Processing {} failed reports", reportsToRetry.size());

                for (FailedReport failedReport : reportsToRetry) {
                    processFailedReport(failedReport);
                }
            }

            // Clean up old successful reports
            cleanupSuccessfulReports();

        } catch (Exception e) {
            log.error("Error processing failed reports", e);
        }
    }

    @Transactional
    public void processFailedReport(FailedReport failedReport) {
        try {
            failedReport.setRetryCount(failedReport.getRetryCount() + 1);
            failedReport.setStatus(ReportStatus.RETRYING);

            boolean success = false;

            if (failedReport.getReportType() == ReportType.INVENTORY) {
                success = retryInventoryReport(failedReport);
            } else if (failedReport.getReportType() == ReportType.USER) {
                success = retryUserReport(failedReport);
            }

            if (success) {
                failedReport.setStatus(ReportStatus.SUCCESS);
                log.info("Successfully sent failed report ID: {}", failedReport.getId());
            } else {
                handleRetryFailure(failedReport);
            }

            failedReportRepository.save(failedReport);

        } catch (Exception e) {
            log.error("Error processing failed report ID: {}", failedReport.getId(), e);
            handleRetryError(failedReport, e.getMessage());
        }
    }

    private boolean retryInventoryReport(FailedReport failedReport) {
        try {
            List<InventoryReportDTO> reports = objectMapper.readValue(
                    failedReport.getReportData(),
                    new TypeReference<List<InventoryReportDTO>>() {}
            );
            sendInventoryReports(reports);
            return true;
        } catch (Exception e) {
            log.error("Failed to retry inventory report ID: {}", failedReport.getId(), e);
            failedReport.setLastError(e.getMessage());
            return false;
        }
    }

    private boolean retryUserReport(FailedReport failedReport) {
        try {
            List<UserReportDTO> reports = objectMapper.readValue(
                    failedReport.getReportData(),
                    new TypeReference<List<UserReportDTO>>() {}
            );
            sendUserReports(reports);
            return true;
        } catch (Exception e) {
            log.error("Failed to retry user report ID: {}", failedReport.getId(), e);
            failedReport.setLastError(e.getMessage());
            return false;
        }
    }

    // Direct HTTP calls for retry (avoiding circular dependency)
    private void sendInventoryReports(List<InventoryReportDTO> reports) throws Exception {
        String url = getServerUrl() + "/api/inventory-reports";
        HttpHeaders headers = buildAuthHeaders();
        HttpEntity<List<InventoryReportDTO>> entity = new HttpEntity<>(reports, headers);
        restTemplate.exchange(url, HttpMethod.POST, entity, Void.class);
    }

    private void sendUserReports(List<UserReportDTO> reports) throws Exception {
        String url = getServerUrl() + "/api/users";
        HttpHeaders headers = buildAuthHeaders();
        HttpEntity<List<UserReportDTO>> entity = new HttpEntity<>(reports, headers);
        restTemplate.exchange(url, HttpMethod.POST, entity, Void.class);
    }

    private String getServerUrl() {
        ClientCredentialEntity entity = credentialRepository.findById("client_singleton")
                .orElseThrow(() -> new IllegalStateException("No client credentials found"));
        return entity.getServerUrl();
    }

    private HttpHeaders buildAuthHeaders() {
        var status = credentialsStore.loadStatus();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (status != null && status.getToken() != null) {
            headers.setBearerAuth(status.getToken());
        }
        return headers;
    }

    private void handleRetryFailure(FailedReport failedReport) {
        if (failedReport.getRetryCount() >= failedReport.getMaxRetries()) {
            failedReport.setStatus(ReportStatus.FAILED_MAX_RETRIES);
            log.error("Failed report ID: {} exceeded maximum retries ({})",
                    failedReport.getId(), failedReport.getMaxRetries());
        } else {
            failedReport.setStatus(ReportStatus.PENDING);
            // Exponential backoff: 2^retryCount minutes
            int delayMinutes = (int) Math.pow(2, failedReport.getRetryCount());
            failedReport.setNextRetryAt(LocalDateTime.now().plusMinutes(Math.min(delayMinutes, 60))); // Max 1 hour
        }
    }

    private void handleRetryError(FailedReport failedReport, String errorMessage) {
        failedReport.setLastError(errorMessage);
        failedReport.setStatus(ReportStatus.PENDING);
        failedReport.setNextRetryAt(LocalDateTime.now().plusMinutes(5)); // Retry in 5 minutes on error
        failedReportRepository.save(failedReport);
    }

    @Transactional
    public void saveFailedInventoryReport(List<InventoryReportDTO> reports, String error) {
        try {
            String reportData = objectMapper.writeValueAsString(reports);

            FailedReport failedReport = new FailedReport();
            failedReport.setReportType(ReportType.INVENTORY);
            failedReport.setReportData(reportData);
            failedReport.setLastError(error);
            failedReport.setStatus(ReportStatus.PENDING);
            failedReport.setNextRetryAt(LocalDateTime.now().plusMinutes(1));

            failedReportRepository.save(failedReport);
            log.info("Saved failed inventory report for retry, count: {}", reports.size());

        } catch (JsonProcessingException e) {
            log.error("Failed to serialize inventory reports for retry storage", e);
        }
    }

    @Transactional
    public void saveFailedUserReport(List<UserReportDTO> reports, String error) {
        try {
            String reportData = objectMapper.writeValueAsString(reports);

            FailedReport failedReport = new FailedReport();
            failedReport.setReportType(ReportType.USER);
            failedReport.setReportData(reportData);
            failedReport.setLastError(error);
            failedReport.setStatus(ReportStatus.PENDING);
            failedReport.setNextRetryAt(LocalDateTime.now().plusMinutes(1));

            failedReportRepository.save(failedReport);
            log.info("Saved failed user report for retry, count: {}", reports.size());

        } catch (JsonProcessingException e) {
            log.error("Failed to serialize user reports for retry storage", e);
        }
    }

    @Scheduled(cron = "${app.report.cleanup.cron:0 0 2 * * ?}") // Daily at 2 AM
    @Transactional
    public void cleanupSuccessfulReports() {
        try {
            // Delete successful reports older than 7 days
            LocalDateTime cutoffDate = LocalDateTime.now().minusDays(7);
            failedReportRepository.deleteByCreatedAtBeforeAndStatus(cutoffDate, ReportStatus.SUCCESS);

            long pendingCount = failedReportRepository.countPendingReports();
            log.info("Cleaned up old successful reports. Pending reports: {}", pendingCount);

        } catch (Exception e) {
            log.error("Error cleaning up successful reports", e);
        }
    }
}