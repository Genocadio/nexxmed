package com.nexxserve.medicine.service;

import com.nexxserve.medicine.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MedicineSearchService {

    private final TherapeuticClassService classService;
    private final GenericService genericService;
    private final VariantService variantService;
    private final BrandService brandService;

    public Map<String, Object> globalSearch(String searchTerm) {
        Map<String, Object> results = new HashMap<>();

        List<TherapeuticClassDto> classes = classService.searchByName(searchTerm);
        List<GenericDto> generics = genericService.searchByName(searchTerm);
        List<VariantDto> variants = variantService.searchByName(searchTerm);
        List<BrandDto> brands = brandService.searchByBrandName(searchTerm);

        results.put("classes", classes);
        results.put("generics", generics);
        results.put("variants", variants);
        results.put("brands", brands);
        results.put("totalResults", classes.size() + generics.size() + variants.size() + brands.size());

        return results;
    }

    public Map<String, Object> unifiedSearch(String query, List<String> types) {
        List<MedicineSearchResultDto> results = new ArrayList<>();
        types = types != null ? types.stream().map(String::toLowerCase).collect(Collectors.toList()) : null;

        // Default behavior: search all types if no specific types provided
        boolean searchAll = types == null || types.isEmpty();
        boolean searchVariants = searchAll || types.contains("variant");
        boolean searchBrands = searchAll || types.contains("brand");
        boolean searchGenerics = searchAll || types.contains("generic");

        // Search variants
        if (searchVariants) {
            List<VariantDto> variants = variantService.searchByName(query);
            results.addAll(variants.stream()
                .map(v -> MedicineSearchResultDto.builder()
                    .id(v.getId())
                    .name(v.getName())
                    .description(v.getNotes())
                    .type("VARIANT")
                    .form(v.getForm())
                    .route(v.getRoute())
                    .strength(v.getStrength())
                    .build())
                .toList());
        }

        // Search brands
        if (searchBrands) {
            List<BrandDto> brands = brandService.searchByBrandName(query);
            results.addAll(brands.stream()
                .map(b -> MedicineSearchResultDto.builder()
                    .id(b.getId())
                    .name(b.getBrandName())
                    .type("BRAND")
                    .manufacturer(b.getManufacturer())
                    .parentId(b.getVariantId())
                    .additionalInfo(b.getCountry())
                    .build())
                .toList());
        }

        // Search generics
        if (searchGenerics) {
            List<GenericDto> generics = genericService.searchByName(query);
            results.addAll(generics.stream()
                .map(g -> MedicineSearchResultDto.builder()
                    .id(g.getId())
                    .name(g.getName())
                    .description(g.getDescription())
                    .type("GENERIC")
                    .additionalInfo(g.getChemicalName())
                    .parentId(g.getClassId())
                    .build())
                .collect(Collectors.toList()));
        }

        Map<String, Object> response = new HashMap<>();
        response.put("results", results);
        response.put("totalResults", results.size());

        return response;
    }

    public Map<String, Object> advancedVariantSearch(String name, String form, String route, String strength) {
        List<VariantDto> results = variantService.findAll().stream()
                .filter(variant -> {
                    boolean matches = true;
                    if (name != null && !name.isEmpty()) {
                        matches &= variant.getName().toLowerCase().contains(name.toLowerCase());
                    }
                    if (form != null && !form.isEmpty()) {
                        matches &= variant.getForm() != null && variant.getForm().toLowerCase().contains(form.toLowerCase());
                    }
                    if (route != null && !route.isEmpty()) {
                        matches &= variant.getRoute() != null && variant.getRoute().toLowerCase().contains(route.toLowerCase());
                    }
                    if (strength != null && !strength.isEmpty()) {
                        matches &= variant.getStrength() != null && variant.getStrength().toLowerCase().contains(strength.toLowerCase());
                    }
                    return matches;
                })
                .toList();

        Map<String, Object> response = new HashMap<>();
        response.put("variants", results);
        response.put("totalResults", results.size());

        return response;
    }
}