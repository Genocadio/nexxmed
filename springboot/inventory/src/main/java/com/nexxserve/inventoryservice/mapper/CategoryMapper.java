package com.nexxserve.inventoryservice.mapper;

import com.nexxserve.inventoryservice.dto.catalog.CategoryDto;
import com.nexxserve.inventoryservice.entity.catalog.CategoryReference;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CategoryMapper {

    public CategoryDto toDto(CategoryReference entity) {
        if (entity == null) return null;

        return CategoryDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .code(entity.getCode())
                .parentId(entity.getParent() != null ? entity.getParent().getId() : null)
                .description(entity.getDescription())
                .build();
    }

    public CategoryReference toEntity(CategoryDto dto) {
        if (dto == null) return null;

        return CategoryReference.builder()
                .name(dto.getName())
                .code(dto.getCode())
                .parent(parentIdToParent(dto.getParentId()))
                .description(dto.getDescription())
                .build();
    }

    public void updateEntityFromDto(CategoryDto dto, CategoryReference entity) {
        if (dto == null || entity == null) return;

        if (dto.getName() != null) entity.setName(dto.getName());
        if (dto.getCode() != null) entity.setCode(dto.getCode());
        if (dto.getParentId() != null) entity.setParent(parentIdToParent(dto.getParentId()));
        if (dto.getDescription() != null) entity.setDescription(dto.getDescription());
    }

    private CategoryReference parentIdToParent(UUID parentId) {
        if (parentId == null) {
            return null;
        }
        CategoryReference parent = new CategoryReference();
        parent.setId(parentId);
        return parent;
    }
}