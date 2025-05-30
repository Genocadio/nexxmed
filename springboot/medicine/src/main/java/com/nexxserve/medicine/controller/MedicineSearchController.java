package com.nexxserve.medicine.controller;

import com.nexxserve.medicine.service.MedicineSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MedicineSearchController {

    private final MedicineSearchService searchService;

    @GetMapping("/global")
    public ResponseEntity<Map<String, Object>> globalSearch(@RequestParam String q) {
        return ResponseEntity.ok(searchService.globalSearch(q));
    }

    @GetMapping("/variants/advanced")
    public ResponseEntity<Map<String, Object>> advancedVariantSearch(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String form,
            @RequestParam(required = false) String route,
            @RequestParam(required = false) String strength) {
        return ResponseEntity.ok(searchService.advancedVariantSearch(name, form, route, strength));
    }
}