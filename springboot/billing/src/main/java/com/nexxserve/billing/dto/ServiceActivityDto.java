package com.nexxserve.billing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceActivityDto {
    private Long id;
    private Long serviceId;
    private String name;
    private BigDecimal price;
    private List<ConsumableQuantityDto> consumables;
}