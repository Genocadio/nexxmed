package com.nexxserve.inventoryservice.service;

import com.nexxserve.inventoryservice.dto.medicine.*;
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
        List<GenericResponseDto> generics = genericService.searchByName(searchTerm);
        List<VariantResponseDto> variants = variantService.searchByName(searchTerm);
        List<BrandResponseDto> brands = brandService.searchByBrandName(searchTerm);

        results.put("classes", classes);
        results.put("generics", generics);
        results.put("variants", variants);
        results.put("brands", brands);
        results.put("totalResults", classes.size() + generics.size() + variants.size() + brands.size());

        return results;
    }

    public Map<String, Object> unifiedSearch(String query, List<String> types) {
        List<Map<String, Object>> results = new ArrayList<>();
        types = types != null ? types.stream().map(String::toLowerCase).collect(Collectors.toList()) : null;

        // Default behavior: search all types if no specific types provided
        boolean searchAll = types == null || types.isEmpty();
        boolean searchVariants = searchAll || types.contains("variant");
        boolean searchBrands = searchAll || types.contains("brand");
        boolean searchGenerics = searchAll || types.contains("generic");

        // Search variants
        if (searchVariants) {
            List<VariantResponseDto> variants = variantService.searchByName(query);
            for (VariantResponseDto variant : variants) {
                Map<String, Object> item = new HashMap<>();
                item.put("id", variant.getId());
                item.put("type", "VARIANT");
                item.put("data", variant);
                results.add(item);
            }
        }

        // Search brands
        if (searchBrands) {
            List<BrandResponseDto> brands = brandService.searchByBrandName(query);
            for (BrandResponseDto brand : brands) {
                Map<String, Object> item = new HashMap<>();
                item.put("id", brand.getId());
                item.put("type", "BRAND");
                item.put("data", brand);
                results.add(item);
            }
        }

        // Search generics
//        if (searchGenerics) {
//            List<GenericDto> generics = genericService.searchByName(query);
//            for (GenericDto generic : generics) {
//                Map<String, Object> item = new HashMap<>();
//                item.put("id", generic.getId());
//                item.put("type", "GENERIC");
//                item.put("data", generic);
//                results.add(item);
//            }
//        }

        Map<String, Object> response = new HashMap<>();
        response.put("results", results);
        response.put("totalResults", results.size());

        return response;
    }

    public Map<String, Object> advancedVariantSearch(String name, String form, String route, String strength) {
        List<VariantResponseDto> results = variantService.findAll().stream()
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