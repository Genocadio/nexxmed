package com.nexxserve.medadmin.service.sync.in;

import com.nexxserve.medadmin.dto.sync.ProductFamilySyncData;
import com.nexxserve.medadmin.dto.sync.ProductFamilyPageResponse;
import com.nexxserve.medadmin.entity.catalog.CategoryReference;
import com.nexxserve.medadmin.entity.catalog.ProductFamily;
import com.nexxserve.medadmin.repository.catalog.CategoryReferenceRepository;
import com.nexxserve.medadmin.repository.catalog.ProductFamilyRepository;
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
public class ProductFamilySyncInService {

    private final ProductFamilyRepository productFamilyRepository;
    private final CategoryReferenceRepository categoryReferenceRepository;
    private final RestTemplate restTemplate;
    private final ClientRepository clientRepository;

    @Transactional
    public void syncProductFamilies(String clientId) {
        try {
            log.info("Starting product family sync for client: {}", clientId);

            // Get baseUrl from database using client ID
            String baseUrl = getBaseUrlFromClient(clientId);

            // Fetch data from source API
            ProductFamilyPageResponse response = fetchProductFamiliesFromSource(baseUrl);

            if (response != null && response.getContent() != null) {
                log.info("Fetched {} product families from source for client: {}", response.getContent().size(), clientId);

                // Process and save each product family
                for (ProductFamilySyncData dto : response.getContent()) {
                    saveProductFamilyFromSync(dto);
                }

                log.info("Product family sync completed successfully for client: {}", clientId);
            } else {
                log.warn("No product family data received from source for client: {}", clientId);
            }

        } catch (Exception e) {
            log.error("Error during product family sync for client: {}", clientId, e);
            throw new RuntimeException("Product family sync failed for client: " + clientId, e);
        }
    }

    private String getBaseUrlFromClient(String clientId) {
        return clientRepository.findByClientId(clientId)
                .map(client -> client.getBaseUrl())
                .orElseThrow(() -> new RuntimeException("Client not found with ID: " + clientId));
    }

    private ProductFamilyPageResponse fetchProductFamiliesFromSource(String baseUrl) {
        try {
            // Add http:// only if not already present
            String formattedBaseUrl = baseUrl.startsWith("http://") || baseUrl.startsWith("https://")
                    ? baseUrl
                    : "http://" + baseUrl;

            String url = String.format("%s/inventory/api/sync/product-families?page=0&size=1000", formattedBaseUrl);

            HttpHeaders headers = new HttpHeaders();
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<ProductFamilyPageResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    ProductFamilyPageResponse.class
            );

            return response.getBody();

        } catch (Exception e) {
            log.error("Error fetching product families from source API", e);
            throw new RuntimeException("Failed to fetch product families from source", e);
        }
    }

    public void saveProductFamilyFromSync(ProductFamilySyncData dto) {
        try {
            Optional<ProductFamily> existingFamily = productFamilyRepository.findById(dto.getId());

            ProductFamily family;
            if (existingFamily.isPresent()) {
                family = existingFamily.get();
                log.debug("Updating existing product family: {}", dto.getId());
            } else {
                family = new ProductFamily();
                log.debug("Creating new product family: {}", dto.getId());
            }

            // Mark as sync operation to preserve timestamps
            family.markAsSyncOperation();

            // Convert DTO to entity
            family.setId(dto.getId());
            family.setName(dto.getName());
            family.setDescription(dto.getDescription());
            family.setBrand(dto.getBrand());
            family.setCreatedBy(dto.getCreatedBy());
            family.setUpdatedBy(dto.getUpdatedBy());
            family.setCreatedAt(dto.getCreatedAt());
            family.setUpdatedAt(dto.getUpdatedAt());
            family.setSyncVersion(dto.getSyncVersion());

            // Set category relationship
            if (dto.getCategoryId() != null) {
                Optional<CategoryReference> category = categoryReferenceRepository.findById(dto.getCategoryId());
                if (category.isPresent()) {
                    family.setCategory(category.get());
                } else {
                    log.error("Category not found for ID: {} while saving product family: {}",
                            dto.getCategoryId(), dto.getId());
                    throw new RuntimeException("Category not found: " + dto.getCategoryId());
                }
            } else {
                log.error("Category ID is required for product family: {}", dto.getId());
                throw new RuntimeException("Category ID is required for product family: " + dto.getId());
            }

            productFamilyRepository.saveAndFlush(family);

            log.debug("Successfully saved product family: {} - {}", dto.getId(), dto.getName());

        } catch (Exception e) {
            log.error("Error saving product family: {}", dto.getId(), e);
            throw new RuntimeException("Failed to save product family: " + dto.getId(), e);
        }
    }
}