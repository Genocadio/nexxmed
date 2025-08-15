package com.nexxserve.medadmin.service.sync.in;

import com.nexxserve.medadmin.dto.sync.BrandSyncData;
import com.nexxserve.medadmin.dto.sync.BrandPageResponse;
import com.nexxserve.medadmin.entity.medicine.Brand;
import com.nexxserve.medadmin.entity.medicine.Variant;
import com.nexxserve.medadmin.repository.clients.ClientRepository;
import com.nexxserve.medadmin.repository.medicine.BrandRepository;
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

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BrandSyncInService {

    private final BrandRepository brandRepository;
    private final VariantRepository variantRepository;
    private final RestTemplate restTemplate;
    private final ClientRepository clientRepository;



    @Value("${sync.api.base-url:localhost:5007}")
    private String baseUrl;

    @Transactional
    public void syncBrands(String clientId) {
        try {
            log.info("Starting brand sync for client: {}", clientId);

            // Get baseUrl from database using client ID
            String baseUrl = getBaseUrlFromClient(clientId);

            // Fetch data from source API
            BrandPageResponse response = fetchBrandsFromSource(baseUrl);

            if (response != null && response.getContent() != null) {
                log.info("Fetched {} brands from source for client: {}", response.getContent().size(), clientId);

                // Process and save each brand
                for (BrandSyncData dto : response.getContent()) {
                    saveBrandFromSync(dto);
                }

                log.info("Brand sync completed successfully for client: {}", clientId);
            } else {
                log.warn("No brand data received from source for client: {}", clientId);
            }

        } catch (Exception e) {
            log.error("Error during brand sync for client: {}", clientId, e);
            throw new RuntimeException("Brand sync failed for client: " + clientId, e);
        }
    }

    private String getBaseUrlFromClient(String clientId) {
        return clientRepository.findByClientId(clientId)
                .map(client -> client.getBaseUrl())
                .orElseThrow(() -> new RuntimeException("Client not found with ID: " + clientId));
    }

    private BrandPageResponse fetchBrandsFromSource(String baseUrl) {
        try {
            // Add http:// only if not already present
            String formattedBaseUrl = baseUrl.startsWith("http://") || baseUrl.startsWith("https://")
                    ? baseUrl
                    : "http://" + baseUrl;

            String url = String.format("%s/inventory/api/sync/brands?page=0&size=1000", formattedBaseUrl);

            HttpHeaders headers = new HttpHeaders();
            HttpEntity<String> entity = new HttpEntity<>(headers);

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