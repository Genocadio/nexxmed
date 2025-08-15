package com.nexxserve.medadmin.service.sync.in;

import com.nexxserve.medadmin.dto.sync.CategoryReferenceSyncData;
import com.nexxserve.medadmin.dto.sync.CategoryReferencePageResponse;
import com.nexxserve.medadmin.entity.catalog.CategoryReference;
import com.nexxserve.medadmin.repository.catalog.CategoryReferenceRepository;
import com.nexxserve.medadmin.repository.clients.ClientRepository;
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
public class CategoryReferenceSyncInService {

    private final CategoryReferenceRepository categoryReferenceRepository;
    private final RestTemplate restTemplate;

    private final ClientRepository clientRepository;

    @Transactional
    public void syncCategoryReferences(String clientId) {
        try {
            log.info("Starting category reference sync for client: {}", clientId);

            // Get baseUrl from database using client ID
            String baseUrl = getBaseUrlFromClient(clientId);

            // Fetch data from source API
            CategoryReferencePageResponse response = fetchCategoryReferencesFromSource(baseUrl);

            if (response != null && response.getContent() != null) {
                log.info("Fetched {} category references from source for client: {}", response.getContent().size(), clientId);

                // Process and save each category reference
                for (CategoryReferenceSyncData dto : response.getContent()) {
                    saveCategoryReferenceFromSync(dto);
                }

                log.info("Category reference sync completed successfully for client: {}", clientId);
            } else {
                log.warn("No category reference data received from source for client: {}", clientId);
            }

        } catch (Exception e) {
            log.error("Error during category reference sync for client: {}", clientId, e);
            throw new RuntimeException("Category reference sync failed for client: " + clientId, e);
        }
    }

    private String getBaseUrlFromClient(String clientId) {
        return clientRepository.findByClientId(clientId)
                .map(client -> client.getBaseUrl())
                .orElseThrow(() -> new RuntimeException("Client not found with ID: " + clientId));
    }

    private CategoryReferencePageResponse fetchCategoryReferencesFromSource(String baseUrl) {
        try {
            // Add http:// only if not already present
            String formattedBaseUrl = baseUrl.startsWith("http://") || baseUrl.startsWith("https://")
                    ? baseUrl
                    : "http://" + baseUrl;

            String url = String.format("%s/inventory/api/sync/categories?page=0&size=1000", formattedBaseUrl);

            HttpHeaders headers = new HttpHeaders();
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<CategoryReferencePageResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    CategoryReferencePageResponse.class
            );

            return response.getBody();

        } catch (Exception e) {
            log.error("Error fetching category references from source API", e);
            throw new RuntimeException("Failed to fetch category references from source", e);
        }
    }

    public void saveCategoryReferenceFromSync(CategoryReferenceSyncData dto) {
        try {
            Optional<CategoryReference> existingCategory = categoryReferenceRepository.findById(dto.getId());

            CategoryReference category;
            if (existingCategory.isPresent()) {
                category = existingCategory.get();
                log.debug("Updating existing category reference: {}", dto.getId());
            } else {
                category = new CategoryReference();
                log.debug("Creating new category reference: {}", dto.getId());
            }

            // Mark as sync operation to preserve timestamps
            category.markAsSyncOperation();

            // Convert DTO to entity
            category.setId(dto.getId());
            category.setName(dto.getName());
            category.setCode(dto.getCode());
            category.setDescription(dto.getDescription());
            category.setCreatedAt(dto.getCreatedAt());
            category.setUpdatedAt(dto.getUpdatedAt());
            category.setSyncVersion(dto.getSyncVersion());

            // Set parent relationship if provided
            // Since parents are synced before children, the parent should already exist
            if (dto.getParentId() != null) {
                Optional<CategoryReference> parent = categoryReferenceRepository.findById(dto.getParentId());
                if (parent.isPresent()) {
                    category.setParent(parent.get());
                } else {
                    log.error("Parent category not found for ID: {} while saving category: {}",
                            dto.getParentId(), dto.getId());
                    throw new RuntimeException("Parent category not found: " + dto.getParentId());
                }
            } else {
                category.setParent(null);
            }

            categoryReferenceRepository.saveAndFlush(category);

            log.debug("Successfully saved category reference: {} - {}", dto.getId(), dto.getName());

        } catch (Exception e) {
            log.error("Error saving category reference: {}", dto.getId(), e);
            throw new RuntimeException("Failed to save category reference: " + dto.getId(), e);
        }
    }
}