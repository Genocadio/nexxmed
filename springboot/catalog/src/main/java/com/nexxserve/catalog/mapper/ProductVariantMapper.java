package com.nexxserve.catalog.mapper;

import com.nexxserve.catalog.dto.ProductVariantDto;
import com.nexxserve.catalog.dto.ProductVariantRequestDto;
import com.nexxserve.catalog.model.entity.ProductFamily;
import com.nexxserve.catalog.model.entity.ProductVariant;
import org.mapstruct.*;

import java.util.UUID;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {ProductInsuranceCoverageMapper.class})
public interface ProductVariantMapper {

    @Mapping(source = "family", target = "family")
    @Mapping(source = "insuranceCoverages", target = "insuranceCoverages")
    ProductVariantDto toDto(ProductVariant entity);

    @Mapping(source = "family.id", target = "family.id")
    @Mapping(source = "insuranceCoverages", target = "insuranceCoverages")
    ProductVariant toEntity(ProductVariantDto dto);

    // Map from RequestDto to entity, handling familyId to family conversion
    @Mapping(source = "familyId", target = "family", qualifiedByName = "mapFamilyReference")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "insuranceCoverages", ignore = true)
    ProductVariant toEntity(ProductVariantRequestDto requestDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "familyId", target = "family", qualifiedByName = "mapFamilyReference")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "insuranceCoverages", ignore = true)
    void updateEntityFromDto(ProductVariantRequestDto requestDto, @MappingTarget ProductVariant entity);

    @Named("mapFamilyReference")
    default ProductFamily mapFamilyReference(UUID familyId) {
        if (familyId == null) {
            return null;
        }
        // Creating just a reference object with ID only
        ProductFamily family = new ProductFamily();
        family.setId(familyId);
        return family;
    }
}