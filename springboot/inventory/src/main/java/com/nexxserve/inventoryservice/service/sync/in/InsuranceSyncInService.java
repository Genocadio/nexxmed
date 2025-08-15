package com.nexxserve.inventoryservice.service.sync.in;

import com.nexxserve.inventoryservice.dto.sync.InsuranceDto;
import com.nexxserve.inventoryservice.dto.sync.InsurancePageResponse;
import com.nexxserve.inventoryservice.entity.Insurance;
import com.nexxserve.inventoryservice.repository.InsuranceRepository;
import com.nexxserve.inventoryservice.security.ClientCredentialsStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class InsuranceSyncInService {

    private final InsuranceRepository insuranceRepository;
    private final RestTemplate restTemplate;
    private final ClientCredentialsStore clientCredentialsStore;

    public String getBaseUrl() {
        String baseUrl = clientCredentialsStore.getServerUrl();
        if (baseUrl == null || baseUrl.isEmpty()) {
            log.error("Base URL not set in ClientCredentialsStore");
            throw new IllegalStateException("Base URL not set in ClientCredentialsStore");
        }
        return baseUrl;
    }

    private HttpHeaders createAuthHeaders() {
        String token = clientCredentialsStore.getToken();
        if (token == null || token.isEmpty()) {
            log.error("No token available for authentication");
            throw new IllegalStateException("No token available - authentication required");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        return headers;
    }

    @Transactional
    public void syncInsurances() {
        try {
            log.info("Starting insurance sync...");

            // Fetch data from source API
            InsurancePageResponse response = fetchInsurancesFromSource();

            if (response != null && response.getContent() != null) {
                log.info("Fetched {} insurances from source", response.getContent().size());

                // Process and save each insurance
                for (InsuranceDto dto : response.getContent()) {
                    saveInsuranceFromSync(dto);
                }

                log.info("Insurance sync completed successfully");
            } else {
                log.warn("No insurance data received from source");
            }

        } catch (Exception e) {
            log.error("Error during insurance sync", e);
            throw new RuntimeException("Insurance sync failed", e);
        }
    }

    private InsurancePageResponse fetchInsurancesFromSource() {
        try {
            String url = String.format("%s/api/sync/insurances?page=0&size=1000", getBaseUrl());

            HttpEntity<String> entity = new HttpEntity<>(createAuthHeaders());

            ResponseEntity<InsurancePageResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    InsurancePageResponse.class
            );

            return response.getBody();

        } catch (Exception e) {
            log.error("Error fetching insurances from source API", e);
            throw new RuntimeException("Failed to fetch insurances from source", e);
        }
    }

    public void saveInsuranceFromSync(InsuranceDto dto) {
        try {
            Optional<Insurance> existingInsurance = insuranceRepository.findById(dto.getId());

            Insurance insurance;
            if (existingInsurance.isPresent()) {
                insurance = existingInsurance.get();
                log.debug("Updating existing insurance: {}", dto.getId());
            } else {
                insurance = new Insurance();
                log.debug("Creating new insurance: {}", dto.getId());
            }

            // Mark as sync operation to preserve timestamps
            insurance.markAsSyncOperation();

            // Copy all fields exactly as received
            insurance.setId(dto.getId());
            insurance.setName(dto.getName());
            insurance.setAbbreviation(dto.getAbbreviation());
            insurance.setDefaultClientContributionPercentage(dto.getDefaultClientContributionPercentage());
            insurance.setDefaultRequiresPreApproval(dto.getDefaultRequiresPreApproval());
            insurance.setActive(dto.getActive());
            insurance.setCreatedBy(dto.getCreatedBy());
            insurance.setUpdatedBy(dto.getUpdatedBy());

            // Preserve original timestamps and version info
            insurance.setUpdatedAt(dto.getUpdatedAt());
            insurance.setCreatedAt(dto.getCreatedAt());
            insurance.setSyncVersion(dto.getSyncVersion());

            insuranceRepository.saveAndFlush(insurance);

            log.debug("Successfully saved insurance: {} - {}", dto.getId(), dto.getName());

        } catch (Exception e) {
            log.error("Error saving insurance: {}", dto.getId(), e);
            throw new RuntimeException("Failed to save insurance: " + dto.getId(), e);
        }
    }
}