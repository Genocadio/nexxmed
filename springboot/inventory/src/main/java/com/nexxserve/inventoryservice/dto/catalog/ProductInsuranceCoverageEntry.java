package com.nexxserve.inventoryservice.dto.catalog;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductInsuranceCoverageEntry {
    private UUID insuranceId;
    private ProductInsuranceCoverageRequestDto coverage;
}