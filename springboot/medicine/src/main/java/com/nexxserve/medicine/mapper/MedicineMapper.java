package com.nexxserve.medicine.mapper;

import com.nexxserve.medicine.dto.*;
import com.nexxserve.medicine.entity.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class MedicineMapper {

    private final InsuranceCoverageMapper insuranceCoverageMapper;

    public MedicineMapper(InsuranceCoverageMapper insuranceCoverageMapper) {
        this.insuranceCoverageMapper = insuranceCoverageMapper;
    }
    public TherapeuticClassDto toDto(TherapeuticClass entity) {
        if (entity == null) return null;

        TherapeuticClassDto dto = new TherapeuticClassDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }

    public TherapeuticClass toEntity(TherapeuticClassDto dto) {
        if (dto == null) return null;

        return TherapeuticClass.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .build();
    }

    public Generic toEntity(GenericRequestDto dto) {
        if (dto == null) return null;

        return Generic.builder()
                .id(dto.getId())
                .name(dto.getName())
                .chemicalName(dto.getChemicalName())
                .description(dto.getDescription())
                .isParent(dto.getIsParent())
                .build();
    }

    public Variant toEntity(VariantRequestDto dto) {
        if (dto == null) return null;

        return Variant.builder()
                .id(dto.getId())
                .name(dto.getName())
                .form(dto.getForm())
                .route(dto.getRoute())
                .tradeName(dto.getTradeName())
                .strength(dto.getStrength())
                .concentration(dto.getConcentration())
                .packaging(dto.getPackaging())
                .notes(dto.getNotes())
                .extraInfo(dto.getExtraInfo())
                .build();
    }

    public Brand toEntity(BrandRequestDto dto) {
        if (dto == null) return null;

        return Brand.builder()
                .id(dto.getId())
                .brandName(dto.getBrandName())
                .manufacturer(dto.getManufacturer())
                .country(dto.getCountry())
                .build();
    }

    public GenericDto toDto(Generic entity) {
        if (entity == null) return null;

        GenericDto dto = new GenericDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setChemicalName(entity.getChemicalName());
        dto.setDescription(entity.getDescription());
        dto.setIsParent(entity.getIsParent());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());

        if (entity.getTherapeuticClass() != null) {
            dto.setClassId(entity.getTherapeuticClass().getId());
            dto.setClassName(entity.getTherapeuticClass().getName());
        }

        if (entity.getInsuranceCoverages() != null && !entity.getInsuranceCoverages().isEmpty()) {
            dto.setInsuranceCoverages(entity.getInsuranceCoverages().stream()
                    .map(insuranceCoverageMapper::toResponseDto)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    public Generic toEntity(GenericDto dto) {
        if (dto == null) return null;

        return Generic.builder()
                .id(dto.getId())
                .name(dto.getName())
                .chemicalName(dto.getChemicalName())
                .description(dto.getDescription())
                .isParent(dto.getIsParent())
                .build();
    }

    public VariantDto toDto(Variant entity) {
        if (entity == null) return null;

        VariantDto dto = new VariantDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setForm(entity.getForm());
        dto.setRoute(entity.getRoute());
        dto.setStrength(entity.getStrength());
        dto.setConcentration(entity.getConcentration());
        dto.setPackaging(entity.getPackaging());
        dto.setNotes(entity.getNotes());
        dto.setExtraInfo(entity.getExtraInfo());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());

        if (entity.getGenerics() != null) {
            dto.setGenericIds(entity.getGenerics().stream()
                    .map(Generic::getId)
                    .collect(Collectors.toList()));
            dto.setGenerics(entity.getGenerics().stream()
                    .map(this::toDto)
                    .collect(Collectors.toList()));
        }

        if (entity.getInsuranceCoverages() != null && !entity.getInsuranceCoverages().isEmpty()) {
            dto.setInsuranceCoverages(entity.getInsuranceCoverages().stream()
                    .map(insuranceCoverageMapper::toResponseDto)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    public Variant toEntity(VariantDto dto) {
        if (dto == null) return null;

        return Variant.builder()
                .id(dto.getId())
                .name(dto.getName())
                .form(dto.getForm())
                .route(dto.getRoute())
                .strength(dto.getStrength())
                .concentration(dto.getConcentration())
                .packaging(dto.getPackaging())
                .notes(dto.getNotes())
                .extraInfo(dto.getExtraInfo())
                .build();
    }

    public BrandDto toDto(Brand entity) {
        if (entity == null) return null;

        BrandDto dto = new BrandDto();
        dto.setId(entity.getId());
        dto.setBrandName(entity.getBrandName());
        dto.setManufacturer(entity.getManufacturer());
        dto.setCountry(entity.getCountry());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());

        if (entity.getVariant() != null) {
            dto.setVariantId(entity.getVariant().getId());
            dto.setVariantName(entity.getVariant().getName());
        }

        if (entity.getInsuranceCoverages() != null && !entity.getInsuranceCoverages().isEmpty()) {
            dto.setInsuranceCoverages(entity.getInsuranceCoverages().stream()
                    .map(insuranceCoverageMapper::toResponseDto)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    public Brand toEntity(BrandDto dto) {
        if (dto == null) return null;

        return Brand.builder()
                .id(dto.getId())
                .brandName(dto.getBrandName())
                .manufacturer(dto.getManufacturer())
                .country(dto.getCountry())
                .build();
    }

    public List<TherapeuticClassDto> toDto(List<TherapeuticClass> entities) {
        return entities.stream().map(this::toDto).collect(Collectors.toList());
    }
}