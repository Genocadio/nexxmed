package com.nexxserve.inventoryservice.mapper;

import com.nexxserve.inventoryservice.dto.catalog.ProductVariantDto;
import com.nexxserve.inventoryservice.dto.catalog.ProductVariantRequestDto;
import com.nexxserve.inventoryservice.entity.catalog.ProductVariant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProductVariantMapper {

    private final ProductFamilyMapper productFamilyMapper;
    private final ProductInsuranceCoverageMapper productInsuranceCoverageMapper;

    public ProductVariantDto toDto(ProductVariant entity) {
        if (entity == null) return null;

        ProductVariantDto dto = ProductVariantDto.builder()
                .id(entity.getId())
                .family(productFamilyMapper.toDto(entity.getFamily()))
                .name(entity.getName())
                .sku(entity.getSku())
                .brand(entity.getBrand())
                .countryOfOrigin(entity.getCountry())
                .color(entity.getColor())
                .weight(entity.getWeight())
                .unitOfMeasure(entity.getUnitOfMeasure())
                .specifications(entity.getSpecifications())
                .createdAt(LocalDateTime.ofInstant(entity.getCreatedAt(), ZoneId.systemDefault()))
                .updatedAt(LocalDateTime.ofInstant(entity.getUpdatedAt(), ZoneId.systemDefault()))
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .build();

        if (entity.getInsuranceCoverages() != null) {
            dto.setInsuranceCoverages(entity.getInsuranceCoverages().stream()
                    .map(productInsuranceCoverageMapper::toResponseDto)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    public ProductVariant toEntity(ProductVariantRequestDto dto) {
        if (dto == null) return null;

        return ProductVariant.builder()
                .name(dto.getName())
                .sku(dto.getSku())
                .brand(dto.getBrand())
                .weight(dto.getWeight())
                .country(dto.getCountryOfOrigin())
                .color(dto.getColor())
                .unitOfMeasure(dto.getUnitOfMeasure())
                .specifications(dto.getSpecifications())
                .build();
    }

    public void updateEntityFromDto(ProductVariantRequestDto dto, ProductVariant entity) {
        if (dto == null || entity == null) return;

        if (dto.getName() != null) entity.setName(dto.getName());
        if (dto.getSku() != null) entity.setSku(dto.getSku());
        if (dto.getBrand() != null) entity.setBrand(dto.getBrand());
        if (dto.getCountryOfOrigin() != null) entity.setCountry(dto.getCountryOfOrigin());
        if (dto.getColor() != null) entity.setColor(dto.getColor());
        if (dto.getUnitOfMeasure() != null) entity.setUnitOfMeasure(dto.getUnitOfMeasure());
        if (dto.getSpecifications() != null) entity.setSpecifications(dto.getSpecifications());
    }
}