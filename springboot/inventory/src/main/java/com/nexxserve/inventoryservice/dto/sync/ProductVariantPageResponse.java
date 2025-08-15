package com.nexxserve.inventoryservice.dto.sync;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariantPageResponse {
    private List<ProductVariantSyncData> content;
    private Pageable pageable;
    private boolean last;
    private int totalPages;
    private long totalElements;
    private boolean first;
    private int numberOfElements;
    private int size;
    private int number;
    private Sort sort;
    private boolean empty;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Pageable {
        private Sort sort;
        private int pageNumber;
        private int pageSize;
        private long offset;
        private boolean paged;
        private boolean unpaged;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Sort {
        private boolean empty;
        private boolean sorted;
        private boolean unsorted;
    }
}