package com.nexxserve.medadmin.service.sync;


import com.nexxserve.medadmin.dto.sync.InsuranceDto;
import com.nexxserve.medadmin.dto.sync.InsurancePageResponse;
import com.nexxserve.medadmin.entity.Insurance;
import com.nexxserve.medadmin.repository.InsuranceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class InsuranceSyncService {

    private final InsuranceRepository insuranceRepository;
    private final RestTemplate restTemplate;


    @Value("${sync.api.base-url:localhost:5007}")
    private String baseUrl;

//    @Value("${sync.api.token}")
//    private String bearerToken;

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
            String url = String.format("http://%s/inventory/api/sync/insurances?page=0&size=1000", baseUrl);

            HttpHeaders headers = new HttpHeaders();
//            headers.setBearerAuth(bearerToken);
            HttpEntity<String> entity = new HttpEntity<>(headers);

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