package com.nexxserve.catalog.service;

import com.nexxserve.catalog.dto.*;
import com.nexxserve.catalog.enums.ProductStatus;
import com.nexxserve.catalog.mapper.ProductInsuranceCoverageMapper;
import com.nexxserve.catalog.mapper.ProductVariantMapper;
import com.nexxserve.catalog.model.entity.Insurance;
import com.nexxserve.catalog.model.entity.ProductInsuranceCoverage;
import com.nexxserve.catalog.model.entity.ProductVariant;
import com.nexxserve.catalog.repository.InsuranceRepository;
import com.nexxserve.catalog.repository.ProductInsuranceCoverageRepository;
import com.nexxserve.catalog.repository.ProductVariantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.nexxserve.catalog.model.entity.ProductFamily;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.nexxserve.catalog.repository.ProductFamilyRepository;

import java.util.List;
import java.util.UUID;

import static com.nexxserve.catalog.service.InsuranceService.getProductInsuranceCoverageDto;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ProductVariantService {

    private final ProductVariantRepository productVariantRepository;
    private final ProductFamilyRepository productFamilyRepository;
    private final ProductVariantMapper productVariantMapper;
    private final InsuranceRepository insuranceRepository;
    private final ProductInsuranceCoverageRepository coverageRepository;
    private final ProductInsuranceCoverageMapper coverageMapper;


    @Transactional
    public ProductInsuranceCoverageDto addInsuranceCoverage(UUID variantId,
                                                            UUID insuranceId,
                                                            ProductInsuranceCoverageRequestDto coverageDto,
                                                            String createdBy) {
        log.debug("Adding insurance coverage for product variant ID: {} and insurance ID: {}", variantId, insuranceId);

        ProductVariant productVariant = productVariantRepository.findById(variantId)
                .orElseThrow(() -> new RuntimeException("Product variant not found with ID: " + variantId));

        Insurance insurance = insuranceRepository.findById(insuranceId)
                .orElseThrow(() -> new RuntimeException("Insurance not found with ID: " + insuranceId));

        // Check if coverage already exists
        return getProductInsuranceCoverageDto(insuranceId, variantId, coverageDto, createdBy, insurance, productVariant, coverageRepository, coverageMapper);
    }

    public List<ProductInsuranceCoverageDto> getInsuranceCoverages(UUID variantId) {
        log.debug("Getting insurance coverages for product variant ID: {}", variantId);

        if (!productVariantRepository.existsById(variantId)) {
            throw new RuntimeException("Product variant not found with ID: " + variantId);
        }

        return coverageRepository.findByProductVariantId(variantId).stream()
                .map(coverageMapper::toDto)
                .toList();
    }

    @Transactional
    public void removeInsuranceCoverage(UUID variantId, UUID coverageId) {
        log.debug("Removing insurance coverage with ID: {} from product variant ID: {}", coverageId, variantId);

        ProductInsuranceCoverage coverage = coverageRepository.findById(coverageId)
                .orElseThrow(() -> new RuntimeException("Insurance coverage not found with ID: " + coverageId));

        if (coverage.getProductVariant() == null || !coverage.getProductVariant().getId().equals(variantId)) {
            throw new RuntimeException("Coverage does not belong to the specified product variant");
        }

        coverageRepository.delete(coverage);
    }

    public Page<ProductVariantDto> findAll(Pageable pageable) {
        log.debug("Finding all product variants with pagination: {}", pageable);
        return productVariantRepository.findAll(pageable)
                .map(productVariantMapper::toDto);
    }

    @Cacheable(value = "productVariants", key = "#id")
    public ProductVariantDto findById(UUID id) {
        log.debug("Finding product variant with id: {}", id);
        ProductVariant productVariant = productVariantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product variant not found with id: " + id));
        return productVariantMapper.toDto(productVariant);
    }

    public ProductVariantDto findBySku(String sku) {
        log.debug("Finding product variant with SKU: {}", sku);
        ProductVariant productVariant = productVariantRepository.findBySku(sku)
                .orElseThrow(() -> new RuntimeException("Product variant not found with SKU: " + sku));
        return productVariantMapper.toDto(productVariant);
    }

    public ProductVariantDto findByUpc(String upc) {
        log.debug("Finding product variant with UPC: {}", upc);
        ProductVariant productVariant = productVariantRepository.findByUpc(upc)
                .orElseThrow(() -> new RuntimeException("Product variant not found with UPC: " + upc));
        return productVariantMapper.toDto(productVariant);
    }

    public List<ProductVariantDto> findByFamilyId(UUID familyId) {
        log.debug("Finding product variants by family id: {}", familyId);
        return productVariantRepository.findByFamilyId(familyId)
                .stream()
                .map(productVariantMapper::toDto)
                .toList();
    }

    public Page<ProductVariantDto> findByStatus(ProductStatus status, Pageable pageable) {
        log.debug("Finding product variants by status: {} with pagination: {}", status, pageable);
        return productVariantRepository.findByStatus(status, pageable)
                .map(productVariantMapper::toDto);
    }

    public Page<ProductVariantDto> searchByTerm(String searchTerm, Pageable pageable) {
        log.debug("Searching product variants by term: {} with pagination: {}", searchTerm, pageable);
        return productVariantRepository.searchByTerm(searchTerm, pageable)
                .map(productVariantMapper::toDto);
    }
    @Transactional
    @CacheEvict(value = "productVariants", allEntries = true)
    public ProductVariantDto create(ProductVariantRequestDto productVariantDto, String createdBy) {
        log.debug("Creating product variant: {}", productVariantDto.getName());

        // Check for duplicate SKU
        if (productVariantRepository.findBySku(productVariantDto.getSku()).isPresent()) {
            throw new RuntimeException("Product variant with SKU already exists: " + productVariantDto.getSku());
        }

        // First convert the DTO to entity
        ProductVariant productVariant = productVariantMapper.toEntity(productVariantDto);

        // Properly load the ProductFamily from database to avoid detached entity issues
        if (productVariantDto.getFamilyId() != null) {
            ProductFamily family = productFamilyRepository.findById(productVariantDto.getFamilyId())
                    .orElseThrow(() -> new RuntimeException("Product family not found with id: " + productVariantDto.getFamilyId()));
            productVariant.setFamily(family);
        }

        productVariant.setCreatedBy(createdBy);
        productVariant.setUpdatedBy(createdBy);

        ProductVariant savedVariant = productVariantRepository.save(productVariant);
        log.info("Created product variant with id: {}", savedVariant.getId());

        addInsuranceCoverages(savedVariant.getId(), productVariantDto.getInsuranceCoverages(), createdBy);
        return productVariantMapper.toDto(savedVariant);
    }

    @Transactional
    protected void addInsuranceCoverages(UUID variantId, List<ProductInsuranceCoverageEntry> coverageEntries, String createdBy) {
        // First, delete all existing coverages
        List<ProductInsuranceCoverage> existingCoverages = coverageRepository.findByProductVariantId(variantId);
        if (!existingCoverages.isEmpty()) {
            log.debug("Removing {} existing insurance coverages for product variant ID: {}",
                    existingCoverages.size(), variantId);

            // Delete each coverage individually to ensure they're properly removed
            for (ProductInsuranceCoverage coverage : existingCoverages) {
                coverageRepository.delete(coverage);
            }

            // Explicitly flush to ensure changes are committed
            coverageRepository.flush();
        }

        // Skip adding new coverages if none provided
        if (coverageEntries == null || coverageEntries.isEmpty()) {
            return;
        }

        // Add new coverages
        for (ProductInsuranceCoverageEntry entry : coverageEntries) {
            try {
                addInsuranceCoverage(variantId, entry.getInsuranceId(), entry.getCoverage(), createdBy);
            } catch (RuntimeException e) {
                log.warn("Failed to add insurance coverage: {}", e.getMessage());
                // Continue with next coverage
            }
        }
    }

    @Transactional
    @CacheEvict(value = "productVariants", key = "#id")
    public ProductVariantDto update(UUID id, ProductVariantRequestDto productVariantDto, String updatedBy) {
        log.debug("Updating product variant with id: {}", id);
        ProductVariant existingVariant = productVariantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product variant not found with id: " + id));

        // Check for duplicate SKU if SKU is being changed
        if (productVariantDto.getSku() != null && !existingVariant.getSku().equals(productVariantDto.getSku()) &&
                productVariantRepository.findBySku(productVariantDto.getSku()).isPresent()) {
            throw new RuntimeException("Product variant with SKU already exists: " + productVariantDto.getSku());
        }

        // Update entity from DTO
        productVariantMapper.updateEntityFromDto(productVariantDto, existingVariant);

        // Properly load the ProductFamily if familyId is provided
        if (productVariantDto.getFamilyId() != null) {
            ProductFamily family = productFamilyRepository.findById(productVariantDto.getFamilyId())
                    .orElseThrow(() -> new RuntimeException("Product family not found with id: " + productVariantDto.getFamilyId()));
            existingVariant.setFamily(family);
        }

        existingVariant.setUpdatedBy(updatedBy);

        ProductVariant savedVariant = productVariantRepository.save(existingVariant);

        addInsuranceCoverages(savedVariant.getId(), productVariantDto.getInsuranceCoverages(), updatedBy);
        return productVariantMapper.toDto(savedVariant);
    }

    @Transactional
    @CacheEvict(value = "productVariants", key = "#id")
    public void delete(UUID id) {
        log.debug("Deleting product variant with id: {}", id);
        if (!productVariantRepository.existsById(id)) {
            throw new RuntimeException("Product variant not found with id: " + id);
        }
        productVariantRepository.deleteById(id);
        log.info("Deleted product variant with id: {}", id);
    }
}