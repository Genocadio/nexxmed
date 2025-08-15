package com.nexxserve.medadmin.service.sync.in;

import com.nexxserve.medadmin.dto.sync.TherapeuticClassPageResponse;
import com.nexxserve.medadmin.dto.sync.TherapeuticClassSyncData;
import com.nexxserve.medadmin.entity.medicine.TherapeuticClass;
import com.nexxserve.medadmin.repository.clients.ClientRepository;
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
@Slf4j
@RequiredArgsConstructor
public class TherapeuticClassSyncInService {

    private final TherapeuticClassRepository therapeuticClassRepository;
    private final RestTemplate restTemplate;
    private final ClientRepository clientRepository;

    @Transactional
    public void syncTherapeuticClasses(String clientId) {
        try {
            log.info("Starting therapeutic class sync for client: {}", clientId);

            // Get baseUrl from database using client ID
            String baseUrl = getBaseUrlFromClient(clientId);

            // Fetch data from source API
            TherapeuticClassPageResponse response = fetchTherapeuticClassesFromSource(baseUrl);

            if (response != null && response.getContent() != null) {
                log.info("Fetched {} therapeutic classes from source for client: {}", response.getContent().size(), clientId);

                // Process and save each therapeutic class
                for (TherapeuticClassSyncData dto : response.getContent()) {
                    saveTherapeuticClassFromSync(dto);
                }

                log.info("Therapeutic class sync completed successfully for client: {}", clientId);
            } else {
                log.warn("No therapeutic class data received from source for client: {}", clientId);
            }

        } catch (Exception e) {
            log.error("Error during therapeutic class sync for client: {}", clientId, e);
            throw new RuntimeException("Therapeutic class sync failed for client: " + clientId, e);
        }
    }

    private String getBaseUrlFromClient(String clientId) {
        return clientRepository.findByClientId(clientId)
                .map(client -> client.getBaseUrl())
                .orElseThrow(() -> new RuntimeException("Client not found with ID: " + clientId));
    }

    private TherapeuticClassPageResponse fetchTherapeuticClassesFromSource(String baseUrl) {
        try {
            // Add http:// only if not already present
            String formattedBaseUrl = baseUrl.startsWith("http://") || baseUrl.startsWith("https://")
                    ? baseUrl
                    : "http://" + baseUrl;

            String url = String.format("%s/inventory/api/sync/classes?page=0&size=1000", formattedBaseUrl);

            HttpHeaders headers = new HttpHeaders();
            HttpEntity<String> entity = new HttpEntity<>(headers);

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