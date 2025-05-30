package com.nexxserve.billing.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InvoiceDto {
    private Long id;

    @NotBlank(message = "Patient ID is required")
    private String patientId;

    // Will be calculated automatically from items - no validation needed
    private BigDecimal totalAmount;

    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // For request only
    @Valid
    private List<InvoiceItemDto> items;

    // For response detail
    private Map<String, Object> patientDetails;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class InvoiceItemDto {
        private Long id;

        private String itemType; // SERVICE, ACTIVITY, or CONSUMABLE

        // Service fields
        private Long serviceId;
        private String serviceName;

        // Activity fields
        private Long activityId;
        private String activityName;

        // Consumable fields
        private Long consumableId;
        private String consumableName;

        @NotNull(message = "Price is required")
        private BigDecimal amount;

        @NotNull(message = "Quantity is required")
        @Positive(message = "Quantity must be positive")
        private Integer quantity;
    }
}