package com.nexxserve.inventoryservice.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ExpiryReport {
    private List<ExpiryItem> expiringItems;
    private List<ExpiryItem> expiredItems;
    private int daysAhead;
}
