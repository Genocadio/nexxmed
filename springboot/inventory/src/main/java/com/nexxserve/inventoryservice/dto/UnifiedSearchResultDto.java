package com.nexxserve.inventoryservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UnifiedSearchResultDto {
    private String searchTerm;

    @Builder.Default
    private List<SearchItemDto> results = new ArrayList<>();

    private long totalResults;
}