package com.nexxserve.inventoryservice.service.sync.in;

import com.nexxserve.inventoryservice.dto.sync.ProductVariantPageResponse;
import com.nexxserve.inventoryservice.dto.sync.ProductVariantSyncData;
import com.nexxserve.inventoryservice.entity.catalog.ProductFamily;
import com.nexxserve.inventoryservice.entity.catalog.ProductVariant;
import com.nexxserve.inventoryservice.repository.catalog.ProductFamilyRepository;
import com.nexxserve.inventoryservice.repository.catalog.ProductVariantRepository;
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
public class ProductVariantSyncInService {

    private final ProductVariantRepository productVariantRepository;
    private final ProductFamilyRepository productFamilyRepository;
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
    public void syncProductVariants() {
        try {
            log.info("Starting product variant sync...");

            // Fetch data from source API
            ProductVariantPageResponse response = fetchProductVariantsFromSource();

            if (response != null && response.getContent() != null) {
                log.info("Fetched {} product variants from source", response.getContent().size());

                // Process and save each product variant
                for (ProductVariantSyncData dto : response.getContent()) {
                    saveProductVariantFromSync(dto);
                }

                log.info("Product variant sync completed successfully");
            } else {
                log.warn("No product variant data received from source");
            }

        } catch (Exception e) {
            log.error("Error during product variant sync", e);
            throw new RuntimeException("Product variant sync failed", e);
        }
    }

    private ProductVariantPageResponse fetchProductVariantsFromSource() {
        try {
            String url = String.format("%/api/sync/product-variants?page=0&size=1000", getBaseUrl());

            HttpEntity<String> entity = new HttpEntity<>(createAuthHeaders());

            ResponseEntity<ProductVariantPageResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    ProductVariantPageResponse.class
            );

            return response.getBody();

        } catch (Exception e) {
            log.error("Error fetching product variants from source API", e);
            throw new RuntimeException("Failed to fetch product variants from source", e);
        }
    }

    public void saveProductVariantFromSync(ProductVariantSyncData dto) {
        try {
            Optional<ProductVariant> existingVariant = productVariantRepository.findById(dto.getId());

            ProductVariant variant;
            if (existingVariant.isPresent()) {
                variant = existingVariant.get();
                log.debug("Updating existing product variant: {}", dto.getId());
            } else {
                variant = new ProductVariant();
                log.debug("Creating new product variant: {}", dto.getId());
            }

            // Mark as sync operation to preserve timestamps
            variant.markAsSyncOperation();

            // Convert DTO to entity
            variant.setId(dto.getId());
            variant.setName(dto.getName());
            variant.setSku(dto.getSku());
            variant.setBrand(dto.getBrand());
            variant.setCountry(dto.getCountry());
            variant.setDimensions(dto.getDimensions());
            variant.setWeight(dto.getWeight());
            variant.setColor(dto.getColor());
            variant.setUnitOfMeasure(dto.getUnitOfMeasure());
            variant.setSpecifications(dto.getSpecifications());
            variant.setCreatedBy(dto.getCreatedBy());
            variant.setUpdatedBy(dto.getUpdatedBy());
            variant.setCreatedAt(dto.getCreatedAt());
            variant.setUpdatedAt(dto.getUpdatedAt());
            variant.setSyncVersion(dto.getSyncVersion());

            // Set product family relationship
            if (dto.getFamilyId() != null) {
                Optional<ProductFamily> family = productFamilyRepository.findById(dto.getFamilyId());
                if (family.isPresent()) {
                    variant.setFamily(family.get());
                } else {
                    log.error("Product family not found for ID: {} while saving product variant: {}",
                            dto.getFamilyId(), dto.getId());
                    throw new RuntimeException("Product family not found: " + dto.getFamilyId());
                }
            } else {
                log.error("Family ID is required for product variant: {}", dto.getId());
                throw new RuntimeException("Family ID is required for product variant: " + dto.getId());
            }

            productVariantRepository.saveAndFlush(variant);

            log.debug("Successfully saved product variant: {} - {}", dto.getId(), dto.getName());

        } catch (Exception e) {
            log.error("Error saving product variant: {}", dto.getId(), e);
            throw new RuntimeException("Failed to save product variant: " + dto.getId(), e);
        }
    }
}