package com.nexxserve.inventoryservice.service.admin;

import com.nexxserve.inventoryservice.dto.admin.InventoryReportDTO;
import com.nexxserve.inventoryservice.dto.admin.UserReportDTO;
import com.nexxserve.inventoryservice.entity.ClientCredentialEntity;
import com.nexxserve.inventoryservice.enums.ReportType;
import com.nexxserve.inventoryservice.event.ReportFailureEvent;
import com.nexxserve.inventoryservice.repository.ClientCredentialRepository;
import com.nexxserve.inventoryservice.security.ClientCredentialsStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RemoteReportService {

    private final RestTemplate restTemplate;
    private final ClientCredentialsStore credentialsStore;
    private final ClientCredentialRepository credentialRepository;
    private final InternetConnectivityService connectivityService;
    private final ApplicationEventPublisher eventPublisher;

    private String getServerUrl() {
        // Always fetch the server URL from the credential entity
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

    public void sendInventoryReport(InventoryReportDTO report) {
        sendInventoryReports(Collections.singletonList(report));
    }

    public void sendInventoryReports(List<InventoryReportDTO> reports) {
        String url = getServerUrl() + "/api/inventory-reports";
        HttpHeaders headers = buildAuthHeaders();
        HttpEntity<List<InventoryReportDTO>> entity = new HttpEntity<>(reports, headers);

        try {
            restTemplate.exchange(url, HttpMethod.POST, entity, Void.class);
            log.info("Inventory reports sent to {}, count: {}", url, reports.size());

        } catch (Exception e) {
            log.error("Failed to send inventory reports to {}, count: {}, error: {}",
                    url, reports.size(), e.getMessage());

            handleReportFailure(e, ReportType.INVENTORY, reports);
        }
    }

    public void sendUserReport(UserReportDTO report) {
        sendUserReports(Collections.singletonList(report));
    }

    public void sendUserReports(List<UserReportDTO> reports) {
        String url = getServerUrl() + "/api/users";
        HttpHeaders headers = buildAuthHeaders();
        HttpEntity<List<UserReportDTO>> entity = new HttpEntity<>(reports, headers);

        try {
            restTemplate.exchange(url, HttpMethod.POST, entity, Void.class);
            log.info("User reports sent to {}, count: {}", url, reports.size());

        } catch (Exception e) {
            log.error("Failed to send user reports to {}, count: {}, error: {}",
                    url, reports.size(), e.getMessage());

            handleReportFailure(e, ReportType.USER, reports);
        }
    }

    private void handleReportFailure(Exception e, ReportType reportType, List<?> reports) {
        boolean isNetworkIssue = isNetworkRelatedError(e);

        if (isNetworkIssue) {
            log.warn("Network connectivity issue detected: {}", e.getMessage());
            connectivityService.markInternetUnavailable();
        }

        // Publish event for retry service to handle
        try {
            eventPublisher.publishEvent(new ReportFailureEvent(this, reportType, reports, e.getMessage()));
        } catch (Exception publishError) {
            log.error("Failed to publish report failure event", publishError);
        }

        // Trigger immediate connectivity check if it's a network issue
        if (isNetworkIssue) {
            connectivityService.checkInternetConnectivity();
        }
    }

    private boolean isNetworkRelatedError(Exception e) {
        if (e instanceof RestClientException) {
            Throwable cause = e.getCause();
            while (cause != null) {
                if (cause instanceof ConnectException ||
                        cause instanceof SocketTimeoutException ||
                        cause instanceof UnknownHostException) {
                    return true;
                }
                cause = cause.getCause();
            }
        }

        String message = e.getMessage();
        if (message != null) {
            message = message.toLowerCase();
            return message.contains("connection") ||
                    message.contains("timeout") ||
                    message.contains("network") ||
                    message.contains("host") ||
                    message.contains("unreachable");
        }

        return false;
    }
}