package com.nexxserve.medadmin.service.sync.in;

import com.nexxserve.medadmin.dto.sync.VariantSyncData;
import com.nexxserve.medadmin.dto.sync.VariantPageResponse;
import com.nexxserve.medadmin.entity.medicine.Variant;
import com.nexxserve.medadmin.entity.medicine.Generic;
import com.nexxserve.medadmin.repository.clients.ClientRepository;
import com.nexxserve.medadmin.repository.medicine.GenericRepository;
import com.nexxserve.medadmin.repository.medicine.VariantRepository;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class VariantSyncInService {

    private final VariantRepository variantRepository;
    private final GenericRepository genericRepository;
    private final RestTemplate restTemplate;
    private final ClientRepository clientRepository;

    @Transactional
    public void syncVariants(String clientId) {
        try {
            log.info("Starting variant sync for client: {}", clientId);

            // Get baseUrl from database using client ID
            String baseUrl = getBaseUrlFromClient(clientId);

            // Fetch data from source API
            VariantPageResponse response = fetchVariantsFromSource(baseUrl);

            if (response != null && response.getContent() != null) {
                log.info("Fetched {} variants from source for client: {}", response.getContent().size(), clientId);

                // Process and save each variant
                for (VariantSyncData dto : response.getContent()) {
                    saveVariantFromSync(dto);
                }

                log.info("Variant sync completed successfully for client: {}", clientId);
            } else {
                log.warn("No variant data received from source for client: {}", clientId);
            }

        } catch (Exception e) {
            log.error("Error during variant sync for client: {}", clientId, e);
            throw new RuntimeException("Variant sync failed for client: " + clientId, e);
        }
    }

    private String getBaseUrlFromClient(String clientId) {
        return clientRepository.findByClientId(clientId)
                .map(client -> client.getBaseUrl())
                .orElseThrow(() -> new RuntimeException("Client not found with ID: " + clientId));
    }

    private VariantPageResponse fetchVariantsFromSource(String baseUrl) {
        try {
            // Add http:// only if not already present
            String formattedBaseUrl = baseUrl.startsWith("http://") || baseUrl.startsWith("https://")
                    ? baseUrl
                    : "http://" + baseUrl;

            String url = String.format("%s/inventory/api/sync/variants?page=0&size=1000", formattedBaseUrl);

            HttpHeaders headers = new HttpHeaders();
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<VariantPageResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    VariantPageResponse.class
            );

            return response.getBody();

        } catch (Exception e) {
            log.error("Error fetching variants from source API", e);
            throw new RuntimeException("Failed to fetch variants from source", e);
        }
    }

    public void saveVariantFromSync(VariantSyncData dto) {
        try {
            Optional<Variant> existingVariant = variantRepository.findById(dto.getId());


            Variant variant;
            if (existingVariant.isPresent()) {
                variant = existingVariant.get();
                log.debug("Updating existing variant: {}", dto.getId());
            } else {
                variant = new Variant();
                log.debug("Creating new variant: {}", dto.getId());
            }

            // Mark as sync operation to preserve timestamps
            variant.markAsSyncOperation();

            // Convert DTO to entity
            variant.setId(dto.getId());
            variant.setName(dto.getName());
            variant.setTradeName(dto.getTradeName());
            variant.setForm(dto.getForm());
            variant.setRoute(dto.getRoute());
            variant.setStrength(dto.getStrength());
            variant.setConcentration(dto.getConcentration());
            variant.setPackaging(dto.getPackaging());
            variant.setNotes(dto.getNotes());
            variant.setCreatedAt(dto.getCreatedAt());
            variant.setUpdatedAt(dto.getUpdatedAt());
            variant.setSyncVersion(dto.getSyncVersion());

            // Set generic relationships if provided
            if (dto.getGenericIds() != null && !dto.getGenericIds().isEmpty()) {
                List<Generic> generics = new ArrayList<>();
                for (UUID genericId : dto.getGenericIds()) {
                    Optional<Generic> generic = genericRepository.findById(genericId);
                    if (generic.isPresent()) {
                        generics.add(generic.get());
                    } else {
                        log.warn("Generic not found for ID: {} while saving variant: {}",
                                genericId, dto.getId());
                        throw new RuntimeException("Generic not found for ID: " + genericId + " while saving variant: " + dto.getId());
                    }
                }
                variant.setGenerics(generics);
            }

            variantRepository.saveAndFlush(variant);

            log.debug("Successfully saved variant: {} - {}", dto.getId(), dto.getName());

        } catch (Exception e) {
            log.error("Error saving variant: {}", dto.getId(), e);
            throw new RuntimeException("Failed to save variant: " + dto.getId(), e);
        }
    }
}