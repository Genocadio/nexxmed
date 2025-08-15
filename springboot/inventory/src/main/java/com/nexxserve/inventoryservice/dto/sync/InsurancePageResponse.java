package com.nexxserve.inventoryservice.dto.sync;


import lombok.Data;

import java.util.List;

@Data
public class InsurancePageResponse {
    private List<InsuranceDto> content;
    private PageableInfo pageable;
    private long totalElements;
    private int totalPages;
    private boolean last;
    private int size;
    private int number;
    private SortInfo sort;
    private int numberOfElements;
    private boolean first;
    private boolean empty;

    @Data
    public static class PageableInfo {
        private int pageNumber;
        private int pageSize;
        private SortInfo sort;
        private int offset;
        private boolean paged;
        private boolean unpaged;
    }

    @Data
    public static class SortInfo {
        private boolean empty;
        private boolean unsorted;
        private boolean sorted;
    }
}