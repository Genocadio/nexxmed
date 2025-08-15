package com.nexxserve.medadmin.service.sync.in;

import com.nexxserve.medadmin.dto.sync.GenericPageResponse;
import com.nexxserve.medadmin.dto.sync.GenericSyncData;
import com.nexxserve.medadmin.entity.medicine.Generic;
import com.nexxserve.medadmin.entity.medicine.TherapeuticClass;
import com.nexxserve.medadmin.repository.clients.ClientRepository;
import com.nexxserve.medadmin.repository.medicine.GenericRepository;
import com.nexxserve.medadmin.repository.medicine.TherapeuticClassRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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


    private final ClientRepository clientRepository;

    @Transactional
    public void syncGenerics(String clientId) {
        try {
            log.info("Starting generic sync for client: {}", clientId);

            // Get baseUrl from database using client ID
            String baseUrl = getBaseUrlFromClient(clientId);

            // Fetch data from source API
            GenericPageResponse response = fetchGenericsFromSource(baseUrl);

            if (response != null && response.getContent() != null) {
                log.info("Fetched {} generics from source for client: {}", response.getContent().size(), clientId);

                // Process and save each generic
                for (GenericSyncData dto : response.getContent()) {
                    saveGenericFromSync(dto);
                }

                log.info("Generic sync completed successfully for client: {}", clientId);
            } else {
                log.warn("No generic data received from source for client: {}", clientId);
            }

        } catch (Exception e) {
            log.error("Error during generic sync for client: {}", clientId, e);
            throw new RuntimeException("Generic sync failed for client: " + clientId, e);
        }
    }

    private String getBaseUrlFromClient(String clientId) {
        return clientRepository.findByClientId(clientId)
                .map(client -> client.getBaseUrl())
                .orElseThrow(() -> new RuntimeException("Client not found with ID: " + clientId));
    }

    private GenericPageResponse fetchGenericsFromSource(String baseUrl) {
        try {
            // Add http:// only if not already present
            String formattedBaseUrl = baseUrl.startsWith("http://") || baseUrl.startsWith("https://")
                    ? baseUrl
                    : "http://" + baseUrl;

            String url = String.format("%s/inventory/api/sync/generics?page=0&size=1000", formattedBaseUrl);

            HttpHeaders headers = new HttpHeaders();
            HttpEntity<String> entity = new HttpEntity<>(headers);

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