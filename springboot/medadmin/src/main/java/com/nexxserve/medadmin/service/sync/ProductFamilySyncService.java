package com.nexxserve.medadmin.service.sync;

import com.nexxserve.medadmin.dto.sync.ProductFamilySyncData;
import com.nexxserve.medadmin.dto.sync.ProductFamilyPageResponse;
import com.nexxserve.medadmin.entity.catalog.CategoryReference;
import com.nexxserve.medadmin.entity.catalog.ProductFamily;
import com.nexxserve.medadmin.repository.catalog.CategoryReferenceRepository;
import com.nexxserve.medadmin.repository.catalog.ProductFamilyRepository;
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
public class ProductFamilySyncService {

    private final ProductFamilyRepository productFamilyRepository;
    private final CategoryReferenceRepository categoryReferenceRepository;
    private final RestTemplate restTemplate;

    @Value("${sync.api.base-url:localhost:5007}")
    private String baseUrl;

    @Transactional
    public void syncProductFamilies() {
        try {
            log.info("Starting product family sync...");

            // Fetch data from source API
            ProductFamilyPageResponse response = fetchProductFamiliesFromSource();

            if (response != null && response.getContent() != null) {
                log.info("Fetched {} product families from source", response.getContent().size());

                // Process and save each product family
                for (ProductFamilySyncData dto : response.getContent()) {
                    saveProductFamilyFromSync(dto);
                }

                log.info("Product family sync completed successfully");
            } else {
                log.warn("No product family data received from source");
            }

        } catch (Exception e) {
            log.error("Error during product family sync", e);
            throw new RuntimeException("Product family sync failed", e);
        }
    }

    private ProductFamilyPageResponse fetchProductFamiliesFromSource() {
        try {
            String url = String.format("http://%s/inventory/api/sync/product-families?page=0&size=1000", baseUrl);

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