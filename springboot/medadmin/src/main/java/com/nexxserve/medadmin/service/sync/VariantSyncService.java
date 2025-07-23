package com.nexxserve.medadmin.service.sync;

import com.nexxserve.medadmin.dto.sync.VariantSyncData;
import com.nexxserve.medadmin.dto.sync.VariantPageResponse;
import com.nexxserve.medadmin.entity.medicine.Variant;
import com.nexxserve.medadmin.entity.medicine.Generic;
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
public class VariantSyncService {

    private final VariantRepository variantRepository;
    private final GenericRepository genericRepository;
    private final RestTemplate restTemplate;

    @Value("${sync.api.base-url:localhost:5007}")
    private String baseUrl;

    @Transactional
    public void syncVariants() {
        try {
            log.info("Starting variant sync...");

            // Fetch data from source API
            VariantPageResponse response = fetchVariantsFromSource();

            if (response != null && response.getContent() != null) {
                log.info("Fetched {} variants from source", response.getContent().size());

                // Process and save each variant
                for (VariantSyncData dto : response.getContent()) {
                    saveVariantFromSync(dto);
                }

                log.info("Variant sync completed successfully");
            } else {
                log.warn("No variant data received from source");
            }

        } catch (Exception e) {
            log.error("Error during variant sync", e);
            throw new RuntimeException("Variant sync failed", e);
        }
    }

    private VariantPageResponse fetchVariantsFromSource() {
        try {
            String url = String.format("http://%s/inventory/api/sync/variants?page=0&size=1000", baseUrl);

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