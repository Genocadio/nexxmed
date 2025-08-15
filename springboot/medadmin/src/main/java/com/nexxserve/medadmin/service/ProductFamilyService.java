package com.nexxserve.medadmin.service;

import com.nexxserve.medadmin.dto.catalog.ProductFamilyDto;
import com.nexxserve.medadmin.dto.catalog.ProductFamilyRequestDto;
import com.nexxserve.medadmin.dto.catalog.ProductInsuranceCoverageRequestDto;
import com.nexxserve.medadmin.entity.Insurance;
import com.nexxserve.medadmin.entity.catalog.CategoryReference;
import com.nexxserve.medadmin.entity.catalog.ProductFamily;
import com.nexxserve.medadmin.entity.catalog.ProductInsuranceCoverage;
import com.nexxserve.medadmin.exception.ResourceNotFoundException;
import com.nexxserve.medadmin.exception.ValidationException;
import com.nexxserve.medadmin.mapper.ProductFamilyMapper;
import com.nexxserve.medadmin.mapper.ProductInsuranceCoverageMapper;
import com.nexxserve.medadmin.repository.InsuranceRepository;
import com.nexxserve.medadmin.repository.catalog.CategoryReferenceRepository;
import com.nexxserve.medadmin.repository.catalog.ProductFamilyRepository;
import com.nexxserve.medadmin.repository.catalog.ProductInsuranceCoverageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductFamilyService {

    private final ProductFamilyRepository productFamilyRepository;
    private final CategoryReferenceRepository categoryRepository;
    private final InsuranceRepository insuranceRepository;
    private final ProductInsuranceCoverageRepository insuranceCoverageRepository;
    private final ProductFamilyMapper productFamilyMapper;
    private final ProductInsuranceCoverageMapper insuranceCoverageMapper;

    @Transactional(readOnly = true)
    public Page<ProductFamilyDto> getAllProductFamilies(Pageable pageable) {
        return productFamilyRepository.findAll(pageable)
                .map(this::mapToDtoWithCoverages);
    }

    public Page<ProductFamilyDto> searchByTerm(String term, Pageable pageable) {
        return productFamilyRepository.findByKeyword(term, pageable)
                .map(this::mapToDtoWithCoverages);
    }

    @Transactional(readOnly = true)
    public ProductFamilyDto getProductFamilyById(UUID id) {
        return productFamilyRepository.findById(id)
                .map(this::mapToDtoWithCoverages)
                .orElseThrow(() -> new ResourceNotFoundException("Product family not found with id: " + id));
    }

    @Transactional
    public ProductFamilyDto createProductFamily(ProductFamilyRequestDto requestDto, String userId) {
        validateProductFamily(requestDto);

        CategoryReference category = categoryRepository.findById(requestDto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + requestDto.getCategoryId()));

        ProductFamily productFamily = productFamilyMapper.toEntity(requestDto);
        productFamily.setCategory(category);

        productFamily.setCreatedBy(userId);
        productFamily.setUpdatedBy(userId);

        ProductFamily savedFamily = productFamilyRepository.save(productFamily);

        // Process insurance coverages if provided
        if (requestDto.getInsuranceCoverages() != null && !requestDto.getInsuranceCoverages().isEmpty()) {
            processInsuranceCoverages(savedFamily, requestDto.getInsuranceCoverages());
        }

        log.info("Created new product family with ID: {}", savedFamily.getId());
        return mapToDtoWithCoverages(savedFamily);
    }

    @Transactional
    public ProductFamilyDto updateProductFamily(UUID id, ProductFamilyRequestDto requestDto, String userId) {
        ProductFamily existingFamily = productFamilyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product family not found with id: " + id));

        validateProductFamily(requestDto);

        // Update category if changed
        if (!existingFamily.getCategory().getId().equals(requestDto.getCategoryId())) {
            CategoryReference category = categoryRepository.findById(requestDto.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + requestDto.getCategoryId()));
            existingFamily.setCategory(category);
        }


        productFamilyMapper.updateEntityFromDto(requestDto, existingFamily);


        // Handle insurance coverages updates
        if (requestDto.getInsuranceCoverages() != null) {
            // Remove existing coverages
            List<ProductInsuranceCoverage> existingCoverages = insuranceCoverageRepository.findByProductFamilyId(id);
            insuranceCoverageRepository.deleteAll(existingCoverages);

            // Add new coverages
            processInsuranceCoverages(existingFamily, requestDto.getInsuranceCoverages());
        }

        existingFamily.setUpdatedBy(userId);

        ProductFamily updatedFamily = productFamilyRepository.save(existingFamily);
        log.info("Updated product family with ID: {}", updatedFamily.getId());
        return mapToDtoWithCoverages(updatedFamily);
    }

    @Transactional
    public void deleteProductFamily(UUID id) {
        ProductFamily family = productFamilyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product family not found with id: " + id));

        // Check if family has insurance coverages
        if (!family.getInsuranceCoverages().isEmpty()) {
            throw new ValidationException("Cannot delete product family with associated insurance coverages");
        }

        productFamilyRepository.delete(family);
        log.info("Deleted product family with ID: {}", id);
    }

    private void validateProductFamily(ProductFamilyRequestDto requestDto) {
        if (requestDto.getCategoryId() == null) {
            throw new ValidationException("Category ID is required");
        }

        // Validate that category exists
        if (!categoryRepository.existsById(requestDto.getCategoryId())) {
            throw new ResourceNotFoundException("Category not found with id: " + requestDto.getCategoryId());
        }



        // Validate insurance coverages if provided
        if (requestDto.getInsuranceCoverages() != null) {
            for (ProductInsuranceCoverageRequestDto coverageDto : requestDto.getInsuranceCoverages()) {
                if (coverageDto.getInsuranceId() == null) {
                    throw new ValidationException("Insurance ID is required for product family insurance coverage");
                }
                if (!insuranceRepository.existsById(coverageDto.getInsuranceId())) {
                    throw new ResourceNotFoundException("Insurance not found with id: " + coverageDto.getInsuranceId());
                }
            }
        }
    }

    private void processInsuranceCoverages(ProductFamily family, List<ProductInsuranceCoverageRequestDto> coverageDtos) {
        List<ProductInsuranceCoverage> coverages = new ArrayList<>();

        for (ProductInsuranceCoverageRequestDto coverageDto : coverageDtos) {
            Insurance insurance = insuranceRepository.findById(coverageDto.getInsuranceId())
                .orElseThrow(() -> new ResourceNotFoundException("Insurance not found with id: " + coverageDto.getInsuranceId()));

            ProductInsuranceCoverage coverage = insuranceCoverageMapper.toEntity(coverageDto);
            coverage.setInsurance(insurance);
            coverage.setProductFamily(family);
            coverage.setProductVariant(null);  // Ensure this is for family only

            coverages.add(insuranceCoverageRepository.save(coverage));
        }

        if (family.getInsuranceCoverages() == null) {
            family.setInsuranceCoverages(coverages);
        } else {
            family.getInsuranceCoverages().addAll(coverages);
        }
    }

    private ProductFamilyDto mapToDtoWithCoverages(ProductFamily family) {
        ProductFamilyDto dto = productFamilyMapper.toDto(family);

        // Include insurance coverages
        if (family.getInsuranceCoverages() != null) {
            dto.setInsuranceCoverages(family.getInsuranceCoverages().stream()
                .map(insuranceCoverageMapper::toResponseDto)
                .collect(Collectors.toList()));
        }

        return dto;
    }
}