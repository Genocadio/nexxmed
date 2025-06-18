package com.nexxserve.medicine.controller;

import com.nexxserve.medicine.service.MedicineSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MedicineSearchController {

    private final MedicineSearchService searchService;

    @GetMapping("/global")
    public ResponseEntity<Map<String, Object>> globalSearch(@RequestParam String q) {
        return ResponseEntity.ok(searchService.globalSearch(q));
    }

    @GetMapping("/unified")
    public ResponseEntity<Map<String, Object>> unifiedSearch(
            @RequestParam(required = true) String q,
            @RequestParam(required = false) List<String> types) {

        if (q == null || q.trim().isEmpty()) {
            throw new IllegalArgumentException("Search query cannot be empty");
        }

        return ResponseEntity.ok(searchService.unifiedSearch(q, types));
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