package com.nexxserve.billing.dto;

import com.nexxserve.billing.model.ServiceActivity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceActivityResponseDto {
    private Long id;
    private String name;
    private BigDecimal price;
    private Long serviceId;
    private List<ConsumableWithQuantityDto> consumables;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ConsumableWithQuantityDto {
        private Long id;
        private String name;
        private BigDecimal price;
        private String description;
        private boolean isGlobal;
        private Long serviceId;
        private Integer quantity;
    }

    public static ServiceActivityResponseDto fromEntity(ServiceActivity activity) {
        List<ConsumableWithQuantityDto> consumableDtos = activity.getActivityConsumables() != null ?
                activity.getActivityConsumables().stream()
                        .map(ac -> ConsumableWithQuantityDto.builder()
                                .id(ac.getConsumable().getId())
                                .name(ac.getConsumable().getName())
                                .price(ac.getConsumable().getPrice())
                                .description(ac.getConsumable().getDescription())
                                .isGlobal(ac.getConsumable().isGlobal())
                                // Fix: Safely convert serviceId string to Long
                                .serviceId(ac.getConsumable().getServiceId() != null ?
                                           Long.valueOf(ac.getConsumable().getServiceId()) : null)
                                .quantity(ac.getQuantity())
                                .build())
                        .collect(Collectors.toList()) :
                null;

        return ServiceActivityResponseDto.builder()
                .id(activity.getId())
                .name(activity.getName())
                .price(activity.getPrice())
                .serviceId(activity.getServiceId())
                .consumables(consumableDtos)
                .createdAt(activity.getCreatedAt())
                .updatedAt(activity.getUpdatedAt())
                .build();
    }

    public static List<ServiceActivityResponseDto> fromEntities(List<ServiceActivity> activities) {
        return activities.stream()
                .map(ServiceActivityResponseDto::fromEntity)
                .collect(Collectors.toList());
    }
}