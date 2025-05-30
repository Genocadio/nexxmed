package com.nexxserve.catalog.mapper;

import com.nexxserve.catalog.dto.InsuranceDto;
import com.nexxserve.catalog.dto.InsuranceRequestDto;
import com.nexxserve.catalog.model.entity.Insurance;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {})
public interface InsuranceMapper {

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "productCoverages", ignore = true)
    Insurance toEntity(InsuranceRequestDto dto);

    InsuranceDto toDto(Insurance entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(InsuranceRequestDto dto, @MappingTarget Insurance entity);
}