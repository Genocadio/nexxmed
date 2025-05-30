package com.nexxserve.medicine.service;

import com.nexxserve.medicine.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
