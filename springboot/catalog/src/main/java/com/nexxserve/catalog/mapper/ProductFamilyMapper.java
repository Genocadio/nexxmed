package com.nexxserve.catalog.mapper;

import com.nexxserve.catalog.dto.ProductFamilyDto;
import com.nexxserve.catalog.dto.ProductFamilyRequestDto;
import com.nexxserve.catalog.model.entity.CategoryReference;
import com.nexxserve.catalog.model.entity.ProductFamily;
import org.mapstruct.*;

import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {CategoryMapper.class})
public interface ProductFamilyMapper {


    // Map from entity to DTO - ensure category is properly mapped
    @Mapping(source = "category", target = "category")
    ProductFamilyDto  toDto(ProductFamily entity);

    // Map from DTO to entity, handling the categoryId to category conversion
    @Mapping(target = "category", expression = "java(mapCategoryId(dto.getCategoryId()))")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    ProductFamily toEntity(ProductFamilyRequestDto dto);

   @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
   @Mapping(target = "category", ignore = true) // Ignore category during mapping
   @Mapping(target = "subCategories", expression = "java(mapSubCategoryIds(dto.getSubCategoryIds(), entity))")
   @Mapping(target = "certifications", expression = "java(dto.getCertifications() != null ? dto.getCertifications() : entity.getCertifications())")
   @Mapping(target = "lifecycleStage", source = "lifecycleStage", defaultValue = "MATURE")
   void updateEntityFromDto(ProductFamilyRequestDto dto, @MappingTarget ProductFamily entity);

    // Add this helper method for subcategories
    default List<CategoryReference> mapSubCategoryIds(List<UUID> ids, ProductFamily entity) {
        if (ids == null) {
            return entity.getSubCategories();
        }
        return ids.stream()
                .map(this::mapCategoryId)
                .collect(java.util.stream.Collectors.toList());
    }

    // Helper method to create a CategoryReference from an ID
    default CategoryReference mapCategoryId(java.util.UUID id) {
        if (id == null) {
            return null;
        }
        CategoryReference categoryReference = new CategoryReference();
        categoryReference.setId(id);
        return categoryReference;
    }
}