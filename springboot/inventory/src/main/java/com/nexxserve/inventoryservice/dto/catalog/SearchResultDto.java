package com.nexxserve.inventoryservice.dto.catalog;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchResultDto {
    private String searchTerm;
    private List<ProductFamilyDto> families;
    private List<ProductVariantDto> variants;
    private long totalFamilies;
    private long totalVariants;
}