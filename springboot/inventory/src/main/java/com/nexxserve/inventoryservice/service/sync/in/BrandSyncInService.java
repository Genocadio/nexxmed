package com.nexxserve.inventoryservice.service.sync.in;

import com.nexxserve.inventoryservice.dto.sync.BrandPageResponse;
import com.nexxserve.inventoryservice.dto.sync.BrandSyncData;
import com.nexxserve.inventoryservice.entity.medicine.Brand;
import com.nexxserve.inventoryservice.entity.medicine.Variant;
import com.nexxserve.inventoryservice.repository.medicine.BrandRepository;
import com.nexxserve.inventoryservice.repository.medicine.VariantRepository;
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
public class BrandSyncInService {

    private final BrandRepository brandRepository;
    private final VariantRepository variantRepository;
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
    public void syncBrands() {
        try {
            log.info("Starting brand sync...");

            // Fetch data from source API
            BrandPageResponse response = fetchBrandsFromSource();

            if (response != null && response.getContent() != null) {
                log.info("Fetched {} brands from source", response.getContent().size());

                // Process and save each brand
                for (BrandSyncData dto : response.getContent()) {
                    saveBrandFromSync(dto);
                }

                log.info("Brand sync completed successfully");
            } else {
                log.warn("No brand data received from source");
            }

        } catch (Exception e) {
            log.error("Error during brand sync", e);
            throw new RuntimeException("Brand sync failed", e);
        }
    }

    private BrandPageResponse fetchBrandsFromSource() {
        try {
            String url = String.format("%s/api/sync/brands?page=0&size=1000", getBaseUrl());

            HttpEntity<String> entity = new HttpEntity<>(createAuthHeaders());

            ResponseEntity<BrandPageResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    BrandPageResponse.class
            );

            return response.getBody();

        } catch (Exception e) {
            log.error("Error fetching brands from source API", e);
            throw new RuntimeException("Failed to fetch brands from source", e);
        }
    }

    public void saveBrandFromSync(BrandSyncData dto) {
        try {
            Optional<Brand> existingBrand = brandRepository.findById(dto.getId());

            Brand brand;
            if (existingBrand.isPresent()) {
                brand = existingBrand.get();
                log.debug("Updating existing brand: {}", dto.getId());
            } else {
                brand = new Brand();
                log.debug("Creating new brand: {}", dto.getId());
            }

            // Mark as sync operation to preserve timestamps
            brand.markAsSyncOperation();

            // Convert DTO to entity
            brand.setId(dto.getId());
            brand.setBrandName(dto.getBrandName());
            brand.setManufacturer(dto.getManufacturer());
            brand.setCountry(dto.getCountry());
            brand.setCreatedAt(dto.getCreatedAt());
            brand.setUpdatedAt(dto.getUpdatedAt());
            brand.setSyncVersion(dto.getSyncVersion());

            // Set variant relationship if provided
            if (dto.getVariantId() != null) {
                Optional<Variant> variant = variantRepository.findById(dto.getVariantId());
                if (variant.isPresent()) {
                    brand.setVariant(variant.get());
                } else {
                    log.warn("Variant not found for ID: {} while saving brand: {}",
                            dto.getVariantId(), dto.getId());
                }
            }

            brandRepository.saveAndFlush(brand);

            log.debug("Successfully saved brand: {} - {}", dto.getId(), dto.getBrandName());

        } catch (Exception e) {
            log.error("Error saving brand: {}", dto.getId(), e);
            throw new RuntimeException("Failed to save brand: " + dto.getId(), e);
        }
    }
}