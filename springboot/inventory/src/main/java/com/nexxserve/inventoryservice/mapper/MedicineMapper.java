package com.nexxserve.inventoryservice.mapper;

import com.nexxserve.inventoryservice.dto.*;
import com.nexxserve.medicine.grpc.MedicineProto;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class MedicineMapper {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");

    public MedicineData toMedicineData(MedicineProto.MedicineResponse response) {
        MedicineData data = new MedicineData();
        data.setId(response.getId());
        data.setName(response.getName());
        data.setProductType(response.getProductType().name());

        // Parse datetime strings to LocalDateTime
        if (!response.getCreatedAt().isEmpty()) {
            data.setCreatedAt(LocalDateTime.parse(response.getCreatedAt(), DATE_FORMATTER));
        }

        if (!response.getUpdatedAt().isEmpty()) {
            data.setUpdatedAt(LocalDateTime.parse(response.getUpdatedAt(), DATE_FORMATTER));
        }

        // Map specialized fields based on product type
        if (response.hasGenericDetails()) {
            data.setGenericDetails(mapGenericDetails(response.getGenericDetails()));
        }

        if (response.hasBrandDetails()) {
            data.setBrandDetails(mapBrandDetails(response.getBrandDetails()));
        }

        if (response.hasVariantDetails()) {
            data.setVariantDetails(mapVariantDetails(response.getVariantDetails()));
        }

        // Map insurance coverages
        data.setInsuranceCoverages(response.getInsuranceCoveragesList().stream()
            .map(this::mapInsuranceCoverage)
            .toList());

        return data;
    }

    private GenericMedicineDetails mapGenericDetails(MedicineProto.GenericDetails details) {
        GenericMedicineDetails genericDetails = new GenericMedicineDetails();
        genericDetails.setChemicalName(details.getChemicalName());
        // Map other fields as needed
        return genericDetails;
    }

    private BrandMedicineDetails mapBrandDetails(MedicineProto.BrandDetails details) {
        BrandMedicineDetails brandDetails = new BrandMedicineDetails();
        brandDetails.setManufacturer(details.getManufacturer());
        brandDetails.setBrandName(details.getBrandName());
        // Map other fields as needed
        return brandDetails;
    }

    private VariantMedicineDetails mapVariantDetails(MedicineProto.VariantDetails details) {
        VariantMedicineDetails variantDetails = new VariantMedicineDetails();
        variantDetails.setForm(details.getForm());
        variantDetails.setRoute(details.getRoute());
        variantDetails.setTradeName(details.getTradeName());
        variantDetails.setStrength(details.getStrength());
        variantDetails.setConcentration(details.getConcentration());
        variantDetails.setPackaging(details.getPackaging());
        variantDetails.setNotes(details.getNotes());

        // Map extra info
        Map<String, String> extraInfo = new HashMap<>();
        for (Map.Entry<String, String> entry : details.getExtraInfoMap().entrySet()) {
            extraInfo.put(entry.getKey(), entry.getValue());
        }
        variantDetails.setExtraInfo(extraInfo);

        // Map generic IDs
        List<String> genericIds = new ArrayList<>(details.getGenericIdsList());
        variantDetails.setGenericIds(genericIds);

        // Map generics
        List<GenericReference> generics = details.getGenericsList().stream()
            .map(generic -> {
                GenericReference ref = new GenericReference();
                ref.setId(generic.getId());
                ref.setName(generic.getName());
                ref.setChemicalName(generic.getChemicalName());
                return ref;
            })
            .toList();
        variantDetails.setGenerics(generics);

        return variantDetails;
    }

    private MedicineInsuranceCoverage mapInsuranceCoverage(MedicineProto.InsuranceCoverage coverage) {
        MedicineInsuranceCoverage insuranceCoverage = new MedicineInsuranceCoverage();
        insuranceCoverage.setId(coverage.getId());
        insuranceCoverage.setInsuranceName(coverage.getInsuranceName());
        insuranceCoverage.setCoverageType(coverage.getCoverageType());
        insuranceCoverage.setCoveragePercentage(coverage.getCoveragePercentage());
        insuranceCoverage.setMaxAmount(coverage.getMaxAmount());
        insuranceCoverage.setNotes(coverage.getNotes());
        return insuranceCoverage;
    }
}