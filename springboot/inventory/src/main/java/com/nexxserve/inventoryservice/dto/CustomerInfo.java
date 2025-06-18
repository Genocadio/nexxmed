package com.nexxserve.inventoryservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CustomerInfo {
    private String customerId;
    private String customerName;
}
