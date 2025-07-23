package com.nexxserve.inventoryservice.service;

import com.nexxserve.inventoryservice.dto.catalog.ProductInsuranceCoverageRequestDto;
import com.nexxserve.inventoryservice.dto.catalog.ProductVariantDto;
import com.nexxserve.inventoryservice.dto.catalog.ProductVariantRequestDto;
import com.nexxserve.inventoryservice.entity.Insurance;
import com.nexxserve.inventoryservice.entity.catalog.ProductFamily;
import com.nexxserve.inventoryservice.entity.catalog.ProductInsuranceCoverage;
import com.nexxserve.inventoryservice.entity.catalog.ProductVariant;
import com.nexxserve.inventoryservice.exception.ResourceNotFoundException;
import com.nexxserve.inventoryservice.exception.ValidationException;
import com.nexxserve.inventoryservice.mapper.ProductInsuranceCoverageMapper;
import com.nexxserve.inventoryservice.mapper.ProductVariantMapper;
import com.nexxserve.inventoryservice.repository.InsuranceRepository;
import com.nexxserve.inventoryservice.repository.catalog.ProductFamilyRepository;
import com.nexxserve.inventoryservice.repository.catalog.ProductInsuranceCoverageRepository;
import com.nexxserve.inventoryservice.repository.catalog.ProductVariantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductVariantService {

    private final ProductVariantRepository variantRepository;
    private final ProductFamilyRepository familyRepository;
    private final InsuranceRepository insuranceRepository;
    private final ProductInsuranceCoverageRepository insuranceCoverageRepository;
    private final ProductVariantMapper variantMapper;
    private final ProductInsuranceCoverageMapper insuranceCoverageMapper;
    private final UserService userService;

    @Transactional(readOnly = true)
    public List<ProductVariantDto> getAllProductVariants() {
        return variantRepository.findAll().stream()
                .map(this::mapToDtoWithCoverages)
                .collect(Collectors.toList());
    }

    public Page<ProductVariantDto> searchByTerm(String searchTerm, Pageable pageable) {
        log.debug("Searching product variants by term: {} with pagination: {}", searchTerm, pageable);
        return variantRepository.findByKeyword(searchTerm, pageable)
                .map(variantMapper::toDto);
    }

    @Transactional(readOnly = true)
    public ProductVariantDto getProductVariantById(UUID id) {
        return variantRepository.findById(id)
                .map(this::mapToDtoWithCoverages)
                .orElseThrow(() -> new ResourceNotFoundException("Product variant not found with id: " + id));
    }

//    @Transactional(readOnly = true)
//    public List<ProductVariantDto> getProductVariantsByFamilyId(UUID familyId) {
//        return variantRepository.findByFamilyId(familyId).stream()
//                .map(this::mapToDtoWithCoverages)
//                .collect(Collectors.toList());
//    }

    @Transactional(readOnly = true)
    public List<ProductVariantDto> getProductVariantsBySku(String sku) {
        return variantRepository.findBySku(sku).stream()
                .map(this::mapToDtoWithCoverages)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductVariantDto> searchProductVariantsByKeyword(String keyword, Pageable pageable) {
        return variantRepository.findByKeyword(keyword, pageable).stream()
                .map(this::mapToDtoWithCoverages)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<ProductVariantDto> getAllProductVariantsPaginated(Pageable pageable) {
        log.debug("Getting all product variants with pagination: {}", pageable);
        return variantRepository.findAll(pageable)
                .map(this::mapToDtoWithCoverages);
    }

    @Transactional
    public ProductVariantDto createProductVariant(ProductVariantRequestDto requestDto) {
        validateProductVariant(requestDto);

        ProductFamily family = familyRepository.findById(requestDto.getFamilyId())
                .orElseThrow(() -> new ResourceNotFoundException("Product family not found with id: " + requestDto.getFamilyId()));

        ProductVariant variant = variantMapper.toEntity(requestDto);
        variant.setFamily(family);
        String id = userService.getCurrentUserId().toString();
        variant.setCreatedBy(id);
        variant.setUpdatedBy(id);

        ProductVariant savedVariant = variantRepository.save(variant);

        // Process insurance coverages if provided
        if (requestDto.getInsuranceCoverages() != null && !requestDto.getInsuranceCoverages().isEmpty()) {
            processInsuranceCoverages(savedVariant, requestDto.getInsuranceCoverages());
        }

        log.info("Created new product variant with ID: {}", savedVariant.getId());
        return mapToDtoWithCoverages(savedVariant);
    }

    @Transactional
    public ProductVariantDto updateProductVariant(UUID id, ProductVariantRequestDto requestDto) {
        ProductVariant existingVariant = variantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product variant not found with id: " + id));

        validateProductVariant(requestDto);

        // Update product family if changed
        if (!existingVariant.getFamily().getId().equals(requestDto.getFamilyId())) {
            ProductFamily family = familyRepository.findById(requestDto.getFamilyId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product family not found with id: " + requestDto.getFamilyId()));
            existingVariant.setFamily(family);
        }

        variantMapper.updateEntityFromDto(requestDto, existingVariant);

        // Handle insurance coverages updates
        if (requestDto.getInsuranceCoverages() != null) {
            // Remove existing coverages
            List<ProductInsuranceCoverage> existingCoverages = insuranceCoverageRepository.findByProductVariantId(id);
            insuranceCoverageRepository.deleteAll(existingCoverages);

            // Add new coverages
            processInsuranceCoverages(existingVariant, requestDto.getInsuranceCoverages());
        }

        ProductVariant updatedVariant = variantRepository.save(existingVariant);
        log.info("Updated product variant with ID: {}", updatedVariant.getId());
        return mapToDtoWithCoverages(updatedVariant);
    }

    @Transactional
    public void deleteProductVariant(UUID id) {
        ProductVariant variant = variantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product variant not found with id: " + id));

        // Check if variant has any insurance coverages before deletion
        if (variant.getInsuranceCoverages() != null && !variant.getInsuranceCoverages().isEmpty()) {
            throw new ValidationException("Cannot delete product variant with associated insurance coverages");
        }

        variantRepository.delete(variant);
        log.info("Deleted product variant with ID: {}", id);
    }

    @Transactional
    public void bulkDeleteProductVariants(List<UUID> ids) {
        for (UUID id : ids) {
            deleteProductVariant(id);
        }
        log.info("Bulk deleted {} product variants", ids.size());
    }

    private void validateProductVariant(ProductVariantRequestDto requestDto) {
        if (requestDto.getFamilyId() == null) {
            throw new ValidationException("Product family ID is required");
        }

        // Validate that product family exists
        if (!familyRepository.existsById(requestDto.getFamilyId())) {
            throw new ResourceNotFoundException("Product family not found with id: " + requestDto.getFamilyId());
        }

        // Add any additional validation rules as needed
        // During update operation
        if (requestDto.getSku() != null && !requestDto.getSku().isBlank()) {
            variantRepository.findBySku(requestDto.getSku())
                .stream()
                .filter(existing -> !existing.getId().equals(requestDto.getId())) // Exclude the current variant
                .findAny()
                .ifPresent(existing -> {
                    throw new ValidationException("Product variant with SKU " + requestDto.getSku() + " already exists");
                });
        }

        // Validate insurance coverages if provided
        if (requestDto.getInsuranceCoverages() != null) {
            for (ProductInsuranceCoverageRequestDto coverageDto : requestDto.getInsuranceCoverages()) {
                if (coverageDto.getInsuranceId() == null) {
                    throw new ValidationException("Insurance ID is required for product variant insurance coverage");
                }
                if (!insuranceRepository.existsById(coverageDto.getInsuranceId())) {
                    throw new ResourceNotFoundException("Insurance not found with id: " + coverageDto.getInsuranceId());
                }
            }
        }
    }

    private void processInsuranceCoverages(ProductVariant variant, List<ProductInsuranceCoverageRequestDto> coverageDtos) {
        List<ProductInsuranceCoverage> coverages = new ArrayList<>();

        for (ProductInsuranceCoverageRequestDto coverageDto : coverageDtos) {
            Insurance insurance = insuranceRepository.findById(coverageDto.getInsuranceId())
                    .orElseThrow(() -> new ResourceNotFoundException("Insurance not found with id: " + coverageDto.getInsuranceId()));

            ProductInsuranceCoverage coverage = insuranceCoverageMapper.toEntity(coverageDto);
            coverage.setInsurance(insurance);
            coverage.setProductVariant(variant);
            coverage.setInsuranceName(insurance.getName());
            coverage.setProductFamily(null);  // Ensure this is for variant only
            coverage.setCreatedBy(userService.getCurrentUserId().toString());
            coverage.setUpdatedBy(userService.getCurrentUserId().toString());

            coverages.add(insuranceCoverageRepository.save(coverage));
        }

        if (variant.getInsuranceCoverages() == null) {
            variant.setInsuranceCoverages(coverages);
        } else {
            variant.getInsuranceCoverages().addAll(coverages);
        }
    }

    private ProductVariantDto mapToDtoWithCoverages(ProductVariant variant) {
        ProductVariantDto dto = variantMapper.toDto(variant);

        // Include insurance coverages
        if (variant.getInsuranceCoverages() != null) {
            dto.setInsuranceCoverages(variant.getInsuranceCoverages().stream()
                    .map(insuranceCoverageMapper::toResponseDto)
                    .collect(Collectors.toList()));
        }

        return dto;
    }
}