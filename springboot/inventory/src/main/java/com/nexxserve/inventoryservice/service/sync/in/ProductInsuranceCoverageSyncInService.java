package com.nexxserve.inventoryservice.service.sync.in;

import com.nexxserve.inventoryservice.dto.sync.ProductInsuranceCoveragePageResponse;
import com.nexxserve.inventoryservice.dto.sync.ProductInsuranceCoverageSyncData;
import com.nexxserve.inventoryservice.entity.Insurance;
import com.nexxserve.inventoryservice.entity.catalog.ProductFamily;
import com.nexxserve.inventoryservice.entity.catalog.ProductInsuranceCoverage;
import com.nexxserve.inventoryservice.entity.catalog.ProductVariant;
import com.nexxserve.inventoryservice.repository.InsuranceRepository;
import com.nexxserve.inventoryservice.repository.catalog.ProductFamilyRepository;
import com.nexxserve.inventoryservice.repository.catalog.ProductInsuranceCoverageRepository;
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
public class ProductInsuranceCoverageSyncInService {

    private final ProductInsuranceCoverageRepository productInsuranceCoverageRepository;
    private final InsuranceRepository insuranceRepository;
    private final ProductFamilyRepository productFamilyRepository;
    private final ProductVariantRepository productVariantRepository;
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
    public void syncProductInsuranceCoverages() {
        try {
            log.info("Starting product insurance coverage sync...");

            // Fetch data from source API
            ProductInsuranceCoveragePageResponse response = fetchProductInsuranceCoveragesFromSource();

            if (response != null && response.getContent() != null) {
                log.info("Fetched {} product insurance coverages from source", response.getContent().size());

                // Process and save each coverage
                for (ProductInsuranceCoverageSyncData dto : response.getContent()) {
                    saveProductInsuranceCoverageFromSync(dto);
                }

                log.info("Product insurance coverage sync completed successfully");
            } else {
                log.warn("No product insurance coverage data received from source");
            }

        } catch (Exception e) {
            log.error("Error during product insurance coverage sync", e);
            throw new RuntimeException("Product insurance coverage sync failed", e);
        }
    }

    private ProductInsuranceCoveragePageResponse fetchProductInsuranceCoveragesFromSource() {
        try {
            String url = String.format("%s/api/sync/product-insurance-coverages?page=0&size=1000", getBaseUrl());

            HttpEntity<String> entity = new HttpEntity<>(createAuthHeaders());

            ResponseEntity<ProductInsuranceCoveragePageResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    ProductInsuranceCoveragePageResponse.class
            );

            return response.getBody();

        } catch (Exception e) {
            log.error("Error fetching product insurance coverages from source API", e);
            throw new RuntimeException("Failed to fetch product insurance coverages from source", e);
        }
    }

    public void saveProductInsuranceCoverageFromSync(ProductInsuranceCoverageSyncData dto) {
        try {
            Optional<ProductInsuranceCoverage> existingCoverage = productInsuranceCoverageRepository.findById(dto.getId());

            ProductInsuranceCoverage coverage;
            if (existingCoverage.isPresent()) {
                coverage = existingCoverage.get();
                log.debug("Updating existing product insurance coverage: {}", dto.getId());
            } else {
                coverage = new ProductInsuranceCoverage();
                log.debug("Creating new product insurance coverage: {}", dto.getId());
            }

            // Mark as sync operation to preserve timestamps
            coverage.markAsSyncOperation();

            // Convert DTO to entity
            coverage.setId(dto.getId());
            coverage.setInsurancePrice(dto.getInsurancePrice());
            coverage.setClientContributionPercentage(dto.getClientContributionPercentage());
            coverage.setRequiresPreApproval(dto.getRequiresPreApproval());
            coverage.setCreatedBy(dto.getCreatedBy());
            coverage.setUpdatedBy(dto.getUpdatedBy());
            coverage.setCreatedAt(dto.getCreatedAt());
            coverage.setUpdatedAt(dto.getUpdatedAt());
            coverage.setSyncVersion(dto.getSyncVersion());

            // Set insurance relationship
            if (dto.getInsuranceId() != null) {
                Optional<Insurance> insurance = insuranceRepository.findById(dto.getInsuranceId());
                if (insurance.isPresent()) {
                    coverage.setInsurance(insurance.get());
                    coverage.setInsuranceName(insurance.get().getName());
                } else {
                    log.error("Insurance not found for ID: {} while saving product coverage: {}",
                            dto.getInsuranceId(), dto.getId());
                    throw new RuntimeException("Insurance not found: " + dto.getInsuranceId());
                }
            } else {
                log.error("Insurance ID is required for product coverage: {}", dto.getId());
                throw new RuntimeException("Insurance ID is required for product coverage: " + dto.getId());
            }

            // Set product relationships (exactly one must be set)
            coverage.setProductFamily(null);
            coverage.setProductVariant(null);

            if (dto.getProductFamilyId() != null) {
                Optional<ProductFamily> family = productFamilyRepository.findById(dto.getProductFamilyId());
                if (family.isPresent()) {
                    coverage.setProductFamily(family.get());
                } else {
                    log.error("Product family not found for ID: {} while saving coverage: {}",
                            dto.getProductFamilyId(), dto.getId());
                    throw new RuntimeException("Product family not found: " + dto.getProductFamilyId());
                }
            } else if (dto.getProductVariantId() != null) {
                Optional<ProductVariant> variant = productVariantRepository.findById(dto.getProductVariantId());
                if (variant.isPresent()) {
                    coverage.setProductVariant(variant.get());
                } else {
                    log.error("Product variant not found for ID: {} while saving coverage: {}",
                            dto.getProductVariantId(), dto.getId());
                    throw new RuntimeException("Product variant not found: " + dto.getProductVariantId());
                }
            } else {
                log.error("At least one product reference (family or variant) is required for coverage: {}", dto.getId());
                throw new RuntimeException("At least one product reference is required for coverage: " + dto.getId());
            }

            productInsuranceCoverageRepository.saveAndFlush(coverage);

            log.debug("Successfully saved product insurance coverage: {} for insurance",
                    dto.getId());

        } catch (Exception e) {
            log.error("Error saving product insurance coverage: {}", dto.getId(), e);
            throw new RuntimeException("Failed to save product insurance coverage: " + dto.getId(), e);
        }
    }
}