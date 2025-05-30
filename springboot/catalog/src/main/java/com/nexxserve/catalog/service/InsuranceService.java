package com.nexxserve.catalog.service;

import com.nexxserve.catalog.dto.InsuranceDto;
import com.nexxserve.catalog.dto.InsuranceRequestDto;
import com.nexxserve.catalog.dto.ProductInsuranceCoverageDto;
import com.nexxserve.catalog.dto.ProductInsuranceCoverageRequestDto;
import com.nexxserve.catalog.enums.InsuranceStatus;
import com.nexxserve.catalog.mapper.InsuranceMapper;
import com.nexxserve.catalog.mapper.ProductInsuranceCoverageMapper;
import com.nexxserve.catalog.model.entity.*;
import com.nexxserve.catalog.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class InsuranceService {

    private final InsuranceRepository insuranceRepository;
    private final ProductInsuranceCoverageRepository coverageRepository;
    private final ProductFamilyRepository productFamilyRepository;
    private final ProductVariantRepository productVariantRepository;
    private final InsuranceMapper insuranceMapper;
    private final ProductInsuranceCoverageMapper coverageMapper;

    // Insurance CRUD operations
    @Cacheable(value = "insurances", key = "#id")
    public InsuranceDto findById(UUID id) {
        log.debug("Finding insurance with id: {}", id);
        Insurance insurance = insuranceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Insurance not found with id: " + id));
        return insuranceMapper.toDto(insurance);
    }

    public List<InsuranceDto> findAll() {
        log.debug("Finding all insurances without pagination");
        return insuranceRepository.findAll().stream()
                .map(insuranceMapper::toDto)
                .toList();
    }

    public Page<InsuranceDto> findAll(Pageable pageable) {
        log.debug("Finding all insurances with pagination: {}", pageable);
        return insuranceRepository.findAll(pageable)
                .map(insuranceMapper::toDto);
    }

    public List<InsuranceDto> findByStatus(InsuranceStatus status) {
        log.debug("Finding insurances by status: {}", status);
        return insuranceRepository.findByStatus(status)
                .stream()
                .map(insuranceMapper::toDto)
                .toList();
    }

    public InsuranceDto findByCode(String code) {
        log.debug("Finding insurance by code: {}", code);
        Insurance insurance = insuranceRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Insurance not found with code: " + code));
        return insuranceMapper.toDto(insurance);
    }

    @Transactional
    @CacheEvict(value = "insurances", allEntries = true)
    public InsuranceDto create(InsuranceRequestDto insuranceDto, String createdBy) {
        log.debug("Creating insurance: {}", insuranceDto.getName());

        // Check for duplicate code
        if (insuranceRepository.findByCode(insuranceDto.getCode()).isPresent()) {
            throw new RuntimeException("Insurance with code already exists: " + insuranceDto.getCode());
        }

        Insurance insurance = insuranceMapper.toEntity(insuranceDto);
        insurance.setCreatedBy(createdBy);
        insurance.setUpdatedBy(createdBy);

        Insurance savedInsurance = insuranceRepository.save(insurance);
        log.info("Created insurance with id: {}", savedInsurance.getId());
        return insuranceMapper.toDto(savedInsurance);
    }

    @Transactional
    @CacheEvict(value = "insurances", key = "#id")
    public InsuranceDto update(UUID id, InsuranceRequestDto insuranceDto, String updatedBy) {
        log.debug("Updating insurance with id: {}", id);
        Insurance existingInsurance = insuranceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Insurance not found with id: " + id));

        // Check for duplicate code if code is being changed
        if (!existingInsurance.getCode().equals(insuranceDto.getCode()) &&
                insuranceRepository.findByCode(insuranceDto.getCode()).isPresent()) {
            throw new RuntimeException("Insurance with code already exists: " + insuranceDto.getCode());
        }

        insuranceMapper.updateEntityFromDto(insuranceDto, existingInsurance);
        existingInsurance.setUpdatedBy(updatedBy);

        Insurance savedInsurance = insuranceRepository.save(existingInsurance);
        return insuranceMapper.toDto(savedInsurance);
    }

    @Transactional
    @CacheEvict(value = "insurances", key = "#id")
    public void delete(UUID id) {
        log.debug("Deleting insurance with id: {}", id);
        if (!insuranceRepository.existsById(id)) {
            throw new RuntimeException("Insurance not found with id: " + id);
        }
        insuranceRepository.deleteById(id);
        log.info("Deleted insurance with id: {}", id);
    }

    // Product Insurance Coverage operations
    public List<ProductInsuranceCoverageDto> findCoveragesByProductFamily(UUID productFamilyId) {
        log.debug("Finding coverages for product family: {}", productFamilyId);
        return coverageRepository.findByProductFamilyId(productFamilyId)
                .stream()
                .map(coverageMapper::toDto)
                .toList();
    }

    public List<ProductInsuranceCoverageDto> findCoveragesByProductVariant(UUID productVariantId) {
        log.debug("Finding coverages for product variant: {}", productVariantId);
        return coverageRepository.findByProductVariantId(productVariantId)
                .stream()
                .map(coverageMapper::toDto)
                .toList();
    }

    public List<ProductInsuranceCoverageDto> findCoveragesByInsurance(UUID insuranceId) {
        log.debug("Finding coverages for insurance: {}", insuranceId);
        return coverageRepository.findByInsuranceId(insuranceId)
                .stream()
                .map(coverageMapper::toDto)
                .toList();
    }

    @Transactional
    public ProductInsuranceCoverageDto createProductFamilyCoverage(
            UUID insuranceId, UUID productFamilyId,
            ProductInsuranceCoverageRequestDto coverageDto, String createdBy) {

        log.debug("Creating coverage for insurance {} and product family {}", insuranceId, productFamilyId);

        Insurance insurance = insuranceRepository.findById(insuranceId)
                .orElseThrow(() -> new RuntimeException("Insurance not found with id: " + insuranceId));

        ProductFamily productFamily = productFamilyRepository.findById(productFamilyId)
                .orElseThrow(() -> new RuntimeException("Product family not found with id: " + productFamilyId));

        // Check if coverage already exists
        return getProductInsuranceCoverageDto(insuranceId, productFamilyId, coverageDto, createdBy, insurance, productFamily, coverageRepository, coverageMapper);
    }

    static ProductInsuranceCoverageDto getProductInsuranceCoverageDto(UUID insuranceId, UUID productFamilyId, ProductInsuranceCoverageRequestDto coverageDto, String createdBy, Insurance insurance, ProductFamily productFamily, ProductInsuranceCoverageRepository coverageRepository, ProductInsuranceCoverageMapper coverageMapper) {
        if (coverageRepository.findByInsuranceIdAndProductFamilyId(insuranceId, productFamilyId).isPresent()) {
            throw new RuntimeException("Coverage already exists for this insurance and product family");
        }

        ProductInsuranceCoverage coverage = coverageMapper.toEntity(coverageDto);
        coverage.setInsurance(insurance);
        coverage.setProductFamily(productFamily);
        coverage.setCreatedBy(createdBy);
        coverage.setUpdatedBy(createdBy);

        ProductInsuranceCoverage savedCoverage = coverageRepository.save(coverage);
        return coverageMapper.toDto(savedCoverage);
    }

    @Transactional
    public ProductInsuranceCoverageDto createProductVariantCoverage(
            UUID insuranceId, UUID productVariantId,
            ProductInsuranceCoverageRequestDto coverageDto, String createdBy) {

        log.debug("Creating coverage for insurance {} and product variant {}", insuranceId, productVariantId);

        Insurance insurance = insuranceRepository.findById(insuranceId)
                .orElseThrow(() -> new RuntimeException("Insurance not found with id: " + insuranceId));

        ProductVariant productVariant = productVariantRepository.findById(productVariantId)
                .orElseThrow(() -> new RuntimeException("Product variant not found with id: " + productVariantId));

        // Check if coverage already exists
        return getProductInsuranceCoverageDto(insuranceId, productVariantId, coverageDto, createdBy, insurance, productVariant, coverageRepository, coverageMapper);
    }

    static ProductInsuranceCoverageDto getProductInsuranceCoverageDto(UUID insuranceId, UUID productVariantId, ProductInsuranceCoverageRequestDto coverageDto, String createdBy, Insurance insurance, ProductVariant productVariant, ProductInsuranceCoverageRepository coverageRepository, ProductInsuranceCoverageMapper coverageMapper) {
        if (coverageRepository.findByInsuranceIdAndProductVariantId(insuranceId, productVariantId).isPresent()) {
            throw new RuntimeException("Coverage already exists for this insurance and product variant");
        }

        ProductInsuranceCoverage coverage = coverageMapper.toEntity(coverageDto);
        coverage.setInsurance(insurance);
        coverage.setProductVariant(productVariant);
        coverage.setCreatedBy(createdBy);
        coverage.setUpdatedBy(createdBy);

        ProductInsuranceCoverage savedCoverage = coverageRepository.save(coverage);
        return coverageMapper.toDto(savedCoverage);
    }

    // Utility methods for coverage calculations
    public BigDecimal calculateClientAmount(UUID coverageId, BigDecimal productPrice) {
        ProductInsuranceCoverage coverage = coverageRepository.findById(coverageId)
                .orElseThrow(() -> new RuntimeException("Coverage not found"));

        BigDecimal price = coverage.getInsurancePrice() != null ?
                coverage.getInsurancePrice() : productPrice;

        BigDecimal clientAmount = price.multiply(coverage.getClientContributionPercentage())
                .divide(BigDecimal.valueOf(100));

        // Apply min/max constraints
        if (coverage.getMinClientContribution() != null &&
                clientAmount.compareTo(coverage.getMinClientContribution()) < 0) {
            clientAmount = coverage.getMinClientContribution();
        }

        if (coverage.getMaxClientContribution() != null &&
                clientAmount.compareTo(coverage.getMaxClientContribution()) > 0) {
            clientAmount = coverage.getMaxClientContribution();
        }

        return clientAmount;
    }

    public BigDecimal calculateInsuranceAmount(UUID coverageId, BigDecimal productPrice) {
        BigDecimal clientAmount = calculateClientAmount(coverageId, productPrice);
        ProductInsuranceCoverage coverage = coverageRepository.findById(coverageId)
                .orElseThrow(() -> new RuntimeException("Coverage not found"));

        BigDecimal price = coverage.getInsurancePrice() != null ?
                coverage.getInsurancePrice() : productPrice;

        return price.subtract(clientAmount);
    }
}