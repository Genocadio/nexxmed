package com.nexxserve.inventoryservice.mapper;

import com.nexxserve.inventoryservice.dto.catalog.CategoryDto;
import com.nexxserve.inventoryservice.dto.catalog.ProductFamilyDto;
import com.nexxserve.inventoryservice.dto.catalog.ProductFamilyRequestDto;
import com.nexxserve.inventoryservice.entity.catalog.CategoryReference;
import com.nexxserve.inventoryservice.entity.catalog.ProductFamily;
import com.nexxserve.inventoryservice.repository.catalog.CategoryReferenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProductFamilyMapper {

    private final CategoryReferenceRepository categoryReferenceRepository;

    public ProductFamilyDto toDto(ProductFamily entity) {
        if (entity == null) {
            return null;
        }

        // Map the category to CategoryDto
        CategoryDto categoryDto = null;
        if (entity.getCategory() != null) {
            categoryDto = CategoryDto.builder()
                    .id(entity.getCategory().getId())
                    .name(entity.getCategory().getName())
                    .build();
        }

        // Extract subcategory IDs

        return ProductFamilyDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .category(categoryDto)
                .createdAt(LocalDateTime.ofInstant(entity.getCreatedAt(), ZoneId.systemDefault()))
                .updatedAt(LocalDateTime.ofInstant(entity.getUpdatedAt(), ZoneId.systemDefault()))
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }

    public ProductFamily toEntity(ProductFamilyRequestDto dto) {
        if (dto == null) {
            return null;
        }

        // Load category
        CategoryReference category = dto.getCategoryId() != null ?
                categoryReferenceRepository.findById(dto.getCategoryId())
                        .orElse(null) : null;

        // Load subcategories


        return ProductFamily.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .category(category)
                .brand(dto.getBrand())

                .build();
    }

    public void updateEntityFromDto(ProductFamilyRequestDto dto, ProductFamily entity) {
        if (dto == null || entity == null) {
            return;
        }

        // Update basic fields
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setBrand(dto.getBrand());

    }
}