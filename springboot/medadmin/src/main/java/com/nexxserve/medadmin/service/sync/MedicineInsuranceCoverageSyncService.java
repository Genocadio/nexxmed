package com.nexxserve.medadmin.service.sync;

import com.nexxserve.medadmin.dto.sync.MedicineInsuranceCoverageSyncData;
import com.nexxserve.medadmin.dto.sync.MedicineInsuranceCoveragePageResponse;
import com.nexxserve.medadmin.entity.Insurance;
import com.nexxserve.medadmin.entity.medicine.Brand;
import com.nexxserve.medadmin.entity.medicine.Generic;
import com.nexxserve.medadmin.entity.medicine.MedicineInsuranceCoverage;
import com.nexxserve.medadmin.entity.medicine.Variant;
import com.nexxserve.medadmin.repository.InsuranceRepository;
import com.nexxserve.medadmin.repository.medicine.BrandRepository;
import com.nexxserve.medadmin.repository.medicine.GenericRepository;
import com.nexxserve.medadmin.repository.medicine.MedicineInsuranceCoverageRepository;
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
public class MedicineInsuranceCoverageSyncService {

    private final MedicineInsuranceCoverageRepository medicineInsuranceCoverageRepository;
    private final InsuranceRepository insuranceRepository;
    private final GenericRepository genericRepository;
    private final BrandRepository brandRepository;
    private final VariantRepository variantRepository;
    private final RestTemplate restTemplate;

    @Value("${sync.api.base-url:localhost:5007}")
    private String baseUrl;

    @Transactional
    public void syncMedicineInsuranceCoverages() {
        try {
            log.info("Starting medicine insurance coverage sync...");

            // Fetch data from source API
            MedicineInsuranceCoveragePageResponse response = fetchMedicineInsuranceCoveragesFromSource();

            if (response != null && response.getContent() != null) {
                log.info("Fetched {} medicine insurance coverages from source", response.getContent().size());

                // Process and save each coverage
                for (MedicineInsuranceCoverageSyncData dto : response.getContent()) {
                    saveMedicineInsuranceCoverageFromSync(dto);
                }

                log.info("Medicine insurance coverage sync completed successfully");
            } else {
                log.warn("No medicine insurance coverage data received from source");
            }

        } catch (Exception e) {
            log.error("Error during medicine insurance coverage sync", e);
            throw new RuntimeException("Medicine insurance coverage sync failed", e);
        }
    }

    private MedicineInsuranceCoveragePageResponse fetchMedicineInsuranceCoveragesFromSource() {
        try {
            String url = String.format("http://%s/inventory/api/sync/medicine-insurance-coverages?page=0&size=1000", baseUrl);

            HttpHeaders headers = new HttpHeaders();
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<MedicineInsuranceCoveragePageResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    MedicineInsuranceCoveragePageResponse.class
            );

            return response.getBody();

        } catch (Exception e) {
            log.error("Error fetching medicine insurance coverages from source API", e);
            throw new RuntimeException("Failed to fetch medicine insurance coverages from source", e);
        }
    }

    public void saveMedicineInsuranceCoverageFromSync(MedicineInsuranceCoverageSyncData dto) {
        try {
            Optional<MedicineInsuranceCoverage> existingCoverage = medicineInsuranceCoverageRepository.findById(dto.getId());

            MedicineInsuranceCoverage coverage;
            if (existingCoverage.isPresent()) {
                coverage = existingCoverage.get();
                log.debug("Updating existing medicine insurance coverage: {}", dto.getId());
            } else {
                coverage = new MedicineInsuranceCoverage();
                log.debug("Creating new medicine insurance coverage: {}", dto.getId());
            }

            // Mark as sync operation to preserve timestamps
            coverage.markAsSyncOperation();

            // Convert DTO to entity
            coverage.setId(dto.getId());
            coverage.setStatus(dto.getStatus());
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
                    log.error("Insurance not found for ID: {} while saving coverage: {}",
                            dto.getInsuranceId(), dto.getId());
                    throw new RuntimeException("Insurance not found: " + dto.getInsuranceId());
                }
            } else {
                log.error("Insurance ID is required for coverage: {}", dto.getId());
                throw new RuntimeException("Insurance ID is required for coverage: " + dto.getId());
            }

            // Set medicine relationships (exactly one must be set)
            coverage.setGeneric(null);
            coverage.setBrand(null);
            coverage.setVariant(null);

            if (dto.getGenericId() != null) {
                Optional<Generic> generic = genericRepository.findById(dto.getGenericId());
                if (generic.isPresent()) {
                    coverage.setGeneric(generic.get());
                } else {
                    log.error("Generic not found for ID: {} while saving coverage: {}",
                            dto.getGenericId(), dto.getId());
                    throw new RuntimeException("Generic not found: " + dto.getGenericId());
                }
            } else if (dto.getBrandId() != null) {
                Optional<Brand> brand = brandRepository.findById(dto.getBrandId());
                if (brand.isPresent()) {
                    coverage.setBrand(brand.get());
                } else {
                    log.error("Brand not found for ID: {} while saving coverage: {}",
                            dto.getBrandId(), dto.getId());
                    throw new RuntimeException("Brand not found: " + dto.getBrandId());
                }
            } else if (dto.getVariantId() != null) {
                Optional<Variant> variant = variantRepository.findById(dto.getVariantId());
                if (variant.isPresent()) {
                    coverage.setVariant(variant.get());
                } else {
                    log.error("Variant not found for ID: {} while saving coverage: {}",
                            dto.getVariantId(), dto.getId());
                    throw new RuntimeException("Variant not found: " + dto.getVariantId());
                }
            } else {
                log.error("At least one medicine reference (generic, brand, or variant) is required for coverage: {}", dto.getId());
                throw new RuntimeException("At least one medicine reference is required for coverage: " + dto.getId());
            }

            medicineInsuranceCoverageRepository.saveAndFlush(coverage);

            log.debug("Successfully saved medicine insurance coverage: {} for insurance:",
                    dto.getId());

        } catch (Exception e) {
            log.error("Error saving medicine insurance coverage: {}", dto.getId(), e);
            throw new RuntimeException("Failed to save medicine insurance coverage: " + dto.getId(), e);
        }
    }
}