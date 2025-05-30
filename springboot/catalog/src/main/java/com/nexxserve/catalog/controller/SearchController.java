package com.nexxserve.catalog.controller;

import com.nexxserve.catalog.dto.ProductFamilyDto;
import com.nexxserve.catalog.dto.ProductVariantDto;
import com.nexxserve.catalog.dto.SearchResultDto;
import com.nexxserve.catalog.service.ProductFamilyService;
import com.nexxserve.catalog.service.ProductVariantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
@Slf4j
public class SearchController {

    private final ProductFamilyService productFamilyService;
    private final ProductVariantService productVariantService;

    @GetMapping("/families")
    public ResponseEntity<Page<ProductFamilyDto>> searchFamilies(
            @RequestParam String term,
            Pageable pageable) {
        log.debug("REST request to search ProductFamilies by term: {}", term);
        Page<ProductFamilyDto> page = productFamilyService.searchByTerm(term, pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/variants")
    public ResponseEntity<Page<ProductVariantDto>> searchVariants(
            @RequestParam String term,
            Pageable pageable) {
        log.debug("REST request to search ProductVariants by term: {}", term);
        Page<ProductVariantDto> page = productVariantService.searchByTerm(term, pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/unified")
    public ResponseEntity<SearchResultDto> unifiedSearch(
            @RequestParam String term,
            @RequestParam(defaultValue = "10") int familyLimit,
            @RequestParam(defaultValue = "20") int variantLimit,
            Pageable pageable) {
        log.debug("REST request for unified search by term: {}", term);

        // Create new Pageable objects with specified sizes
        Pageable familyPageable = PageRequest.of(0, familyLimit, pageable.getSort());
        Pageable variantPageable = PageRequest.of(0, variantLimit, pageable.getSort());

        Page<ProductFamilyDto> families = productFamilyService.searchByTerm(term, familyPageable);
        Page<ProductVariantDto> variants = productVariantService.searchByTerm(term, variantPageable);

        SearchResultDto result = SearchResultDto.builder()
                .searchTerm(term)
                .families(families.getContent())
                .variants(variants.getContent())
                .totalFamilies(families.getTotalElements())
                .totalVariants(variants.getTotalElements())
                .build();

        return ResponseEntity.ok(result);
    }

    @GetMapping("/suggest")
    public ResponseEntity<List<String>> searchSuggestions(
            @RequestParam String term,
            @RequestParam(defaultValue = "10") int limit) {
        log.debug("REST request for search suggestions by term: {}", term);

        // This would typically be implemented with a search engine like Elasticsearch
        // For now, returning empty list as placeholder
        List<String> suggestions = List.of();

        return ResponseEntity.ok(suggestions);
    }
}