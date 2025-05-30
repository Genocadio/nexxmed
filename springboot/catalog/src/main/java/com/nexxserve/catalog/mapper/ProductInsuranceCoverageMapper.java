package com.nexxserve.catalog.mapper;

import com.nexxserve.catalog.dto.ProductInsuranceCoverageDto;
import com.nexxserve.catalog.dto.ProductInsuranceCoverageRequestDto;
import com.nexxserve.catalog.model.entity.ProductInsuranceCoverage;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {InsuranceMapper.class, ProductFamilyMapper.class, ProductVariantMapper.class})
public interface ProductInsuranceCoverageMapper {

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "insurance", ignore = true)
    @Mapping(target = "productFamily", ignore = true)
    @Mapping(target = "productVariant", ignore = true)
    @Mapping(target = "insuranceCoveragePercentage", ignore = true) // Calculated in entity
    ProductInsuranceCoverage toEntity(ProductInsuranceCoverageRequestDto dto);

    @Mapping(target = "insuranceId", source = "insurance.id")
    @Mapping(target = "insuranceName", source = "insurance.name")
    @Mapping(target = "productFamilyId", source = "productFamily.id", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "productFamilyName", source = "productFamily.name", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "productVariantId", source = "productVariant.id", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "productVariantName", source = "productVariant.name", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    ProductInsuranceCoverageDto toDto(ProductInsuranceCoverage entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(ProductInsuranceCoverageRequestDto dto, @MappingTarget ProductInsuranceCoverage entity);
}