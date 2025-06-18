package com.nexxserve.catalog.service;

import com.nexxserve.catalog.dto.ProductFamilyDto;
import com.nexxserve.catalog.dto.ProductFamilyRequestDto;
import com.nexxserve.catalog.dto.ProductInsuranceCoverageDto;
import com.nexxserve.catalog.dto.ProductInsuranceCoverageRequestDto;
import com.nexxserve.catalog.enums.ProductStatus;
import com.nexxserve.catalog.exception.ProductFamilyNotFoundException;
import com.nexxserve.catalog.exception.ResourceNotFoundException;
import com.nexxserve.catalog.mapper.ProductFamilyMapper;
import com.nexxserve.catalog.mapper.ProductInsuranceCoverageMapper;
import com.nexxserve.catalog.model.entity.CategoryReference;
import com.nexxserve.catalog.model.entity.Insurance;
import com.nexxserve.catalog.model.entity.ProductFamily;
import com.nexxserve.catalog.model.entity.ProductInsuranceCoverage;
import com.nexxserve.catalog.repository.CategoryReferenceRepository;
import com.nexxserve.catalog.repository.InsuranceRepository;
import com.nexxserve.catalog.repository.ProductFamilyRepository;
import com.nexxserve.catalog.repository.ProductInsuranceCoverageRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static com.nexxserve.catalog.service.InsuranceService.getProductInsuranceCoverageDto;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ProductFamilyService {

    private final ProductFamilyRepository productFamilyRepository;
    private final ProductFamilyMapper productFamilyMapper;
    private final EntityManagerFactory entityManagerFactory;
    private final EntityManager entityManager;
    private final CategoryReferenceRepository categoryReferenceRepository;
    private final InsuranceRepository insuranceRepository;
    private final ProductInsuranceCoverageRepository coverageRepository;
    private final ProductInsuranceCoverageMapper coverageMapper;


    @Transactional
    public ProductInsuranceCoverageDto addInsuranceCoverage(UUID familyId,
                                                            UUID insuranceId,
                                                            ProductInsuranceCoverageRequestDto coverageDto,
                                                            String createdBy) {
        log.debug("Adding insurance coverage for product family ID: {} and insurance ID: {}", familyId, insuranceId);

        ProductFamily productFamily = productFamilyRepository.findById(familyId)
                .orElseThrow(() -> new ProductFamilyNotFoundException(familyId));

        Insurance insurance = insuranceRepository.findById(insuranceId)
                .orElseThrow(() -> new RuntimeException("Insurance not found with id: " + insuranceId));

        // Check if coverage already exists
        return getProductInsuranceCoverageDto(insuranceId, familyId, coverageDto, createdBy, insurance, productFamily, coverageRepository, coverageMapper);
    }

    public List<ProductInsuranceCoverageDto> getInsuranceCoverages(UUID familyId) {
        log.debug("Getting insurance coverages for product family ID: {}", familyId);

        if (!productFamilyRepository.existsById(familyId)) {
            throw new ProductFamilyNotFoundException(familyId);
        }

        return coverageRepository.findByProductFamilyId(familyId).stream()
                .map(coverageMapper::toDto)
                .toList();
    }

    @Transactional
    public void removeInsuranceCoverage(UUID familyId, UUID coverageId) {
        log.debug("Removing insurance coverage with ID: {} from product family ID: {}", coverageId, familyId);

        ProductInsuranceCoverage coverage = coverageRepository.findById(coverageId)
                .orElseThrow(() -> new RuntimeException("Insurance coverage not found with ID: " + coverageId));

        if (coverage.getProductFamily() == null || !coverage.getProductFamily().getId().equals(familyId)) {
            throw new RuntimeException("Coverage does not belong to the specified product family");
        }

        coverageRepository.delete(coverage);
    }

    @Cacheable(value = "productFamilies", key = "#id")
    public ProductFamilyDto findById(UUID id) {
        log.debug("Finding product family with id: {}", id);
        ProductFamily productFamily = productFamilyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product family not found with id: " + id));
        return productFamilyMapper.toDto(productFamily);
    }

    public Page<ProductFamilyDto> findAll(Pageable pageable) {
        log.debug("Finding all product families with pagination: {}", pageable);
        return productFamilyRepository.findAll(pageable)
                .map(productFamilyMapper::toDto);
    }

    public Page<ProductFamilyDto> findByStatus(ProductStatus status, Pageable pageable) {
        log.debug("Finding product families by status: {} with pagination: {}", status, pageable);
        return productFamilyRepository.findByStatus(status, pageable)
                .map(productFamilyMapper::toDto);
    }

    public List<ProductFamilyDto> findByBrand(String brand) {
        log.debug("Finding product families by brand: {}", brand);
        return productFamilyRepository.findByBrand(brand)
                .stream()
                .map(productFamilyMapper::toDto)
                .toList();
    }

    public Page<ProductFamilyDto> searchByTerm(String searchTerm, Pageable pageable) {
        log.debug("Searching product families by term: {} with pagination: {}", searchTerm, pageable);
        return productFamilyRepository.searchByTerm(searchTerm, pageable)
                .map(productFamilyMapper::toDto);
    }

    @Transactional
    @CacheEvict(value = "productFamilies", allEntries = true)
    public ProductFamilyDto create(ProductFamilyRequestDto productFamilyDto, String createdBy) {
        log.debug("Creating product family: {}", productFamilyDto.getName());
        ProductFamily productFamily = productFamilyMapper. toEntity(productFamilyDto);
        productFamily.setCreatedBy(createdBy);
        productFamily.setUpdatedBy(createdBy);

        ProductFamily savedFamily = productFamilyRepository.save(productFamily);
        log.info("Created product family with id: {}", savedFamily.getId());
        return productFamilyMapper.toDto(savedFamily);
    }


    @Transactional
    @CacheEvict(value = "productFamilies", key = "#id")
    public ProductFamilyDto update(UUID id, ProductFamilyRequestDto productFamilyDto, String updatedBy) {
        log.debug("Updating product family with id: {}", id);
        ProductFamily existingFamily = productFamilyRepository.findById(id)
                .orElseThrow(() -> new ProductFamilyNotFoundException(id));

        // If the categoryId is provided and different from current, load the complete category
        if (productFamilyDto.getCategoryId() != null &&
                (existingFamily.getCategory() == null ||
                        !productFamilyDto.getCategoryId().equals(existingFamily.getCategory().getId()))) {
            CategoryReference fullCategory = categoryReferenceRepository.findById(productFamilyDto.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found with ID: " + productFamilyDto.getCategoryId()));
            existingFamily.setCategory(fullCategory);
        }

        productFamilyMapper.updateEntityFromDto(productFamilyDto, existingFamily);
        existingFamily.setUpdatedBy(updatedBy);

        ProductFamily savedFamily = productFamilyRepository.save(existingFamily);
        return productFamilyMapper.toDto(savedFamily);
    }
    @Transactional
    @CacheEvict(value = "productFamilies", key = "#id")
    public void delete(UUID id) {
        log.debug("Deleting product family with id: {}", id);
        if (!productFamilyRepository.existsById(id)) {
            throw new RuntimeException("Product family not found with id: " + id);
        }
        productFamilyRepository.deleteById(id);
        log.info("Deleted product family with id: {}", id);
    }
}