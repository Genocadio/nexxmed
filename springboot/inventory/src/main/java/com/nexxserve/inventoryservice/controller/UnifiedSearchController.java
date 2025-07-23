package com.nexxserve.inventoryservice.controller;

import com.nexxserve.inventoryservice.dto.SearchItemDto;
import com.nexxserve.inventoryservice.dto.UnifiedSearchResultDto;
import com.nexxserve.inventoryservice.service.MedicineSearchService;
import com.nexxserve.inventoryservice.service.ProductFamilyService;
import com.nexxserve.inventoryservice.service.ProductVariantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
@Slf4j
public class UnifiedSearchController {

    private final ProductFamilyService productFamilyService;
    private final ProductVariantService productVariantService;
    private final MedicineSearchService medicineSearchService;

    @GetMapping("/unified")
    public ResponseEntity<UnifiedSearchResultDto> unifiedSearch(
            @RequestParam String q,
            @RequestParam(required = false, defaultValue = "all") String domain,
            @RequestParam(defaultValue = "20") int limit,
            Pageable pageable) {

        log.debug("REST request for unified search by term: {} with domain filter: {}", q, domain);

        List<SearchItemDto> results = new ArrayList<>();
        UnifiedSearchResultDto resultDto = new UnifiedSearchResultDto();
        resultDto.setSearchTerm(q);

        // Create pageable with the specified limit for each domain
        int domainLimit = "all".equalsIgnoreCase(domain) ? limit / 2 : limit;
        Pageable limitedPageable = PageRequest.of(0, domainLimit, pageable.getSort());

        // Search product catalog
        if ("all".equalsIgnoreCase(domain) || "catalog".equalsIgnoreCase(domain)) {
            // Get product families
//            Page<com.nexxserve.inventoryservice.dto.catalog.ProductFamilyDto> families =
//                    productFamilyService.searchByTerm(q, limitedPageable);
//
//            families.getContent().forEach(family -> {
//                results.add(SearchItemDto.builder()
//                        .id(family.getId().toString())
//                        .domain("catalog")
//                        .type("PRODUCT_FAMILY")
//                        .data(family)
//                        .build());
//            });

            // Get product variants
            Page<com.nexxserve.inventoryservice.dto.catalog.ProductVariantDto> variants =
                    productVariantService.searchByTerm(q, limitedPageable);

            variants.getContent().forEach(variant -> {
                results.add(SearchItemDto.builder()
                        .id(variant.getId().toString())
                        .domain("catalog")
                        .type("PRODUCT_VARIANT")
                        .data(variant)
                        .build());
            });
        }

        // Search medicine domain
        if ("all".equalsIgnoreCase(domain) || "medicine".equalsIgnoreCase(domain)) {
            Map<String, Object> medicineResults = medicineSearchService.unifiedSearch(q, null);
            List<Map<String, Object>> medicineItems = (List<Map<String, Object>>) medicineResults.get("results");

            if (medicineItems != null) {
                for (Map<String, Object> item : medicineItems) {
                    String type = (String) item.get("type");
                    Object itemData = item.get("data");  // Extract the actual data
                    results.add(SearchItemDto.builder()
                            .id(String.valueOf(item.get("id")))
                            .domain("medicine")
                            .type(type)
                            .data(itemData)  // Use the extracted data directly
                            .build());
                }
            }
        }

        resultDto.setResults(results);
        resultDto.setTotalResults(results.size());

        return ResponseEntity.ok(resultDto);
    }
}