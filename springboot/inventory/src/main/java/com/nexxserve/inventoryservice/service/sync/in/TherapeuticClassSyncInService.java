package com.nexxserve.inventoryservice.service.sync.in;

import com.nexxserve.inventoryservice.dto.sync.TherapeuticClassPageResponse;
import com.nexxserve.inventoryservice.dto.sync.TherapeuticClassSyncData;
import com.nexxserve.inventoryservice.entity.medicine.TherapeuticClass;
import com.nexxserve.inventoryservice.repository.medicine.TherapeuticClassRepository;
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
@Slf4j
@RequiredArgsConstructor
public class TherapeuticClassSyncInService {

    private final TherapeuticClassRepository therapeuticClassRepository;
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
    public void syncTherapeuticClasses() {
        try {
            log.info("Starting therapeutic class sync...");

            // Fetch data from source API
            TherapeuticClassPageResponse response = fetchTherapeuticClassesFromSource();

            if (response != null && response.getContent() != null) {
                log.info("Fetched {} therapeutic classes from source", response.getContent().size());

                // Process and save each therapeutic class
                for (TherapeuticClassSyncData dto : response.getContent()) {
                    saveTherapeuticClassFromSync(dto);
                }

                log.info("Therapeutic class sync completed successfully");
            } else {
                log.warn("No therapeutic class data received from source");
            }

        } catch (Exception e) {
            log.error("Error during therapeutic class sync", e);
            throw new RuntimeException("Therapeutic class sync failed", e);
        }
    }

    private TherapeuticClassPageResponse fetchTherapeuticClassesFromSource() {
        try {
            String url = String.format("%s/api/sync/classes?page=0&size=1000", getBaseUrl());

            HttpEntity<String> entity = new HttpEntity<>(createAuthHeaders());

            ResponseEntity<TherapeuticClassPageResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    TherapeuticClassPageResponse.class
            );

            return response.getBody();

        } catch (Exception e) {
            log.error("Error fetching therapeutic classes from source API", e);
            throw new RuntimeException("Failed to fetch therapeutic classes from source", e);
        }
    }

    public void saveTherapeuticClassFromSync(TherapeuticClassSyncData dto) {
        try {
            Optional<TherapeuticClass> existingClass = therapeuticClassRepository.findById(dto.getId());

            TherapeuticClass therapeuticClass;
            if (existingClass.isPresent()) {
                therapeuticClass = existingClass.get();
                log.debug("Updating existing therapeutic class: {}", dto.getId());
            } else {
                therapeuticClass = new TherapeuticClass();
                log.debug("Creating new therapeutic class: {}", dto.getId());
            }

            // Mark as sync operation to preserve timestamps
            therapeuticClass.markAsSyncOperation();

            // Convert DTO to entity
            therapeuticClass.setId(dto.getId());
            therapeuticClass.setName(dto.getName());
            therapeuticClass.setDescription(dto.getDescription());
            therapeuticClass.setCreatedAt(dto.getCreatedAt());
            therapeuticClass.setUpdatedAt(dto.getUpdatedAt());
            therapeuticClass.setSyncVersion(dto.getSyncVersion());

            therapeuticClassRepository.saveAndFlush(therapeuticClass);

            log.debug("Successfully saved therapeutic class: {} - {}", dto.getId(), dto.getName());

        } catch (Exception e) {
            log.error("Error saving therapeutic class: {}", dto.getId(), e);
            throw new RuntimeException("Failed to save therapeutic class: " + dto.getId(), e);
        }
    }
}