package com.nexxserve.inventoryservice.service.sync.in;

import com.nexxserve.inventoryservice.dto.sync.GenericPageResponse;
import com.nexxserve.inventoryservice.dto.sync.GenericSyncData;
import com.nexxserve.inventoryservice.entity.medicine.Generic;
import com.nexxserve.inventoryservice.entity.medicine.TherapeuticClass;
import com.nexxserve.inventoryservice.repository.medicine.GenericRepository;
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
@RequiredArgsConstructor
@Slf4j
public class GenericSyncInService {

    private final GenericRepository genericRepository;
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
    public void syncGenerics() {
        try {
            log.info("Starting generic sync...");

            // Fetch data from source API
            GenericPageResponse response = fetchGenericsFromSource();

            if (response != null && response.getContent() != null) {
                log.info("Fetched {} generics from source", response.getContent().size());

                // Process and save each generic
                for (GenericSyncData dto : response.getContent()) {
                    saveGenericFromSync(dto);
                }

                log.info("Generic sync completed successfully");
            } else {
                log.warn("No generic data received from source");
            }

        } catch (Exception e) {
            log.error("Error during generic sync", e);
            throw new RuntimeException("Generic sync failed", e);
        }
    }

    private GenericPageResponse fetchGenericsFromSource() {
        try {
            String url = String.format("%s/api/sync/generics?page=0&size=1000", getBaseUrl());

            HttpEntity<String> entity = new HttpEntity<>(createAuthHeaders());

            ResponseEntity<GenericPageResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    GenericPageResponse.class
            );

            return response.getBody();

        } catch (Exception e) {
            log.error("Error fetching generics from source API", e);
            throw new RuntimeException("Failed to fetch generics from source", e);
        }
    }

    public void saveGenericFromSync(GenericSyncData dto) {
        try {
            Optional<Generic> existingGeneric = genericRepository.findById(dto.getId());

            Generic generic;
            if (existingGeneric.isPresent()) {
                generic = existingGeneric.get();
                log.debug("Updating existing generic: {}", dto.getId());
            } else {
                generic = new Generic();
                log.debug("Creating new generic: {}", dto.getId());
            }

            // Mark as sync operation to preserve timestamps
            generic.markAsSyncOperation();

            // Convert DTO to entity
            generic.setId(dto.getId());
            generic.setName(dto.getName());
            generic.setChemicalName(dto.getChemicalName());
            generic.setDescription(dto.getDescription());
            generic.setIsParent(dto.getIsParent());
            generic.setCreatedAt(dto.getCreatedAt());
            generic.setUpdatedAt(dto.getUpdatedAt());
            generic.setSyncVersion(dto.getSyncVersion());

            // Set therapeutic class relationship if provided
            if (dto.getTherapeuticClassId() != null) {
                Optional<TherapeuticClass> therapeuticClass = therapeuticClassRepository.findById(dto.getTherapeuticClassId());
                if (therapeuticClass.isPresent()) {
                    generic.setTherapeuticClass(therapeuticClass.get());
                } else {
                    log.warn("Therapeutic class not found for ID: {} while saving generic: {}",
                            dto.getTherapeuticClassId(), dto.getId());
                }
            }

            genericRepository.saveAndFlush(generic);

            log.debug("Successfully saved generic: {} - {}", dto.getId(), dto.getName());

        } catch (Exception e) {
            log.error("Error saving generic: {}", dto.getId(), e);
            throw new RuntimeException("Failed to save generic: " + dto.getId(), e);
        }
    }
}