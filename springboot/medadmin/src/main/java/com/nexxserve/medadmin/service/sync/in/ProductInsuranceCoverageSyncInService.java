package com.nexxserve.medadmin.service.sync.in;

import com.nexxserve.medadmin.dto.sync.ProductInsuranceCoverageSyncData;
import com.nexxserve.medadmin.dto.sync.ProductInsuranceCoveragePageResponse;
import com.nexxserve.medadmin.entity.Insurance;
import com.nexxserve.medadmin.entity.catalog.ProductFamily;
import com.nexxserve.medadmin.entity.catalog.ProductInsuranceCoverage;
import com.nexxserve.medadmin.entity.catalog.ProductVariant;
import com.nexxserve.medadmin.repository.InsuranceRepository;
import com.nexxserve.medadmin.repository.catalog.ProductFamilyRepository;
import com.nexxserve.medadmin.repository.catalog.ProductInsuranceCoverageRepository;
import com.nexxserve.medadmin.repository.catalog.ProductVariantRepository;
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
public class ProductInsuranceCoverageSyncInService {

    private final ProductInsuranceCoverageRepository productInsuranceCoverageRepository;
    private final InsuranceRepository insuranceRepository;
    private final ProductFamilyRepository productFamilyRepository;
    private final ProductVariantRepository productVariantRepository;
    private final RestTemplate restTemplate;
    private final ClientRepository clientRepository;

    @Transactional
    public void syncProductInsuranceCoverages(String clientId) {
        try {
            log.info("Starting product insurance coverage sync for client: {}", clientId);

            // Get baseUrl from database using client ID
            String baseUrl = getBaseUrlFromClient(clientId);

            // Fetch data from source API
            ProductInsuranceCoveragePageResponse response = fetchProductInsuranceCoveragesFromSource(baseUrl);

            if (response != null && response.getContent() != null) {
                log.info("Fetched {} product insurance coverages from source for client: {}", response.getContent().size(), clientId);

                // Process and save each coverage
                for (ProductInsuranceCoverageSyncData dto : response.getContent()) {
                    saveProductInsuranceCoverageFromSync(dto);
                }

                log.info("Product insurance coverage sync completed successfully for client: {}", clientId);
            } else {
                log.warn("No product insurance coverage data received from source for client: {}", clientId);
            }

        } catch (Exception e) {
            log.error("Error during product insurance coverage sync for client: {}", clientId, e);
            throw new RuntimeException("Product insurance coverage sync failed for client: " + clientId, e);
        }
    }

    private String getBaseUrlFromClient(String clientId) {
        return clientRepository.findByClientId(clientId)
                .map(client -> client.getBaseUrl())
                .orElseThrow(() -> new RuntimeException("Client not found with ID: " + clientId));
    }

    private ProductInsuranceCoveragePageResponse fetchProductInsuranceCoveragesFromSource(String baseUrl) {
        try {
            // Add http:// only if not already present
            String formattedBaseUrl = baseUrl.startsWith("http://") || baseUrl.startsWith("https://")
                    ? baseUrl
                    : "http://" + baseUrl;

            String url = String.format("%s/inventory/api/sync/product-insurance-coverages?page=0&size=1000", formattedBaseUrl);

            HttpHeaders headers = new HttpHeaders();
            HttpEntity<String> entity = new HttpEntity<>(headers);

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