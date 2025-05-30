package com.nexxserve.catalog.mapper;

import com.nexxserve.catalog.dto.CategoryDto;
import com.nexxserve.catalog.model.entity.CategoryReference;
import org.mapstruct.*;

import java.util.UUID;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CategoryMapper {

    @Mapping(source = "parent.id", target = "parentId")
    CategoryDto toDto(CategoryReference entity);

    @Mapping(source = "parentId", target = "parent", qualifiedByName = "mapParentReference")
    CategoryReference toEntity(CategoryDto dto);

//    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
//    @Mapping(source = "parentId", target = "parent", qualifiedByName = "mapParentReference")
//    void updateEntityFromDto(CategoryDto dto, @MappingTarget CategoryReference entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "parentId", target = "parent", qualifiedByName = "mapParentReference")
    default void updateEntityFromDto(CategoryDto dto, @MappingTarget CategoryReference entity) {
        // Handle parent reference explicitly
        if (dto.getParentId() != null) {
            // For non-null parentId, create a new parent reference
            CategoryReference parent = new CategoryReference();
            parent.setId(dto.getParentId());
            entity.setParent(parent);
        } else {
            // For null parentId, explicitly set parent to null
            entity.setParent(null);
        }

        // Copy all other properties
        if (dto.getName() != null) entity.setName(dto.getName());
        if (dto.getCode() != null) entity.setCode(dto.getCode());
        if (dto.getLevel() != null) entity.setLevel(dto.getLevel());
        if (dto.getDisplayOrder() != null) entity.setDisplayOrder(dto.getDisplayOrder());
        entity.setIsActive(dto.getIsActive());
        entity.setIcon(dto.getIcon());
        entity.setDescription(dto.getDescription());
        entity.setTaxCategory(dto.getTaxCategory());
        entity.setRegulatoryCategory(dto.getRegulatoryCategory());
        entity.setLocalizedNames(dto.getLocalizedNames());
        entity.setMetadata(dto.getMetadata());
    }
    @Named("mapParentReference")
    default CategoryReference mapParentReference(UUID parentId) {
        if (parentId == null) {
            return null; // Return null when no parent ID is provided
        }

        // Create a reference with just the ID set
        CategoryReference parent = new CategoryReference();
        parent.setId(parentId);
        return parent;
    }
}