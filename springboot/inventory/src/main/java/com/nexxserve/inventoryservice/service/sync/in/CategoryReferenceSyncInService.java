package com.nexxserve.inventoryservice.service.sync.in;

import com.nexxserve.inventoryservice.dto.sync.CategoryReferencePageResponse;
import com.nexxserve.inventoryservice.dto.sync.CategoryReferenceSyncData;
import com.nexxserve.inventoryservice.entity.catalog.CategoryReference;
import com.nexxserve.inventoryservice.repository.catalog.CategoryReferenceRepository;
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
public class CategoryReferenceSyncInService {

    private final CategoryReferenceRepository categoryReferenceRepository;
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
    public void syncCategoryReferences() {
        try {
            log.info("Starting category reference sync...");

            // Fetch data from source API
            CategoryReferencePageResponse response = fetchCategoryReferencesFromSource();

            if (response != null && response.getContent() != null) {
                log.info("Fetched {} category references from source", response.getContent().size());

                // Process and save each category reference
                for (CategoryReferenceSyncData dto : response.getContent()) {
                    saveCategoryReferenceFromSync(dto);
                }

                log.info("Category reference sync completed successfully");
            } else {
                log.warn("No category reference data received from source");
            }

        } catch (Exception e) {
            log.error("Error during category reference sync", e);
            throw new RuntimeException("Category reference sync failed", e);
        }
    }

    private CategoryReferencePageResponse fetchCategoryReferencesFromSource() {
        try {
            String url = String.format("%s/api/sync/categories?page=0&size=1000", getBaseUrl());

            HttpEntity<String> entity = new HttpEntity<>(createAuthHeaders());

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