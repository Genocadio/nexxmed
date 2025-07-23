package com.nexxserve.inventoryservice.mapper;

import com.nexxserve.inventoryservice.dto.*;
import com.nexxserve.inventoryservice.dto.stock.CreateStockEntryRequest;
import com.nexxserve.inventoryservice.dto.stock.StockDetails;
import com.nexxserve.inventoryservice.dto.stock.StockEntryResponse;
import com.nexxserve.inventoryservice.dto.stock.StockMetadata;
import com.nexxserve.inventoryservice.entity.inventory.StockEntry;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class StockEntryMapper {

    public StockEntry toEntity(CreateStockEntryRequest request) {
        return StockEntry.builder()
                .referenceId(request.getProductReference().getReferenceId())
                .productType(request.getProductReference().getProductType())
                .sourceService(request.getProductReference().getSourceService())
                .quantity(request.getQuantity())
                .originalQuantity(request.getQuantity())
                .unitPrice(request.getUnitPrice())
                .costPrice(request.getCostPrice())
                .entryDate(LocalDateTime.now())
                .expirationDate(request.getExpirationDate())
                .batchNumber(request.getBatchNumber())
                .supplierId(request.getSupplierInfo() != null ?
                        request.getSupplierInfo().getSupplierId() : null)
                .supplierName(request.getSupplierInfo() != null ?
                        request.getSupplierInfo().getSupplierName() : null)
                .createdBy("system") // Could be dynamic based on authentication
                .build();
    }

    public StockEntryResponse toResponse(StockEntry entity) {
        return StockEntryResponse.builder()
                .id(entity.getId())
                .productReference(ProductReference.builder()
                        .referenceId(entity.getReferenceId())
                        .productType(entity.getProductType())
                        .sourceService(entity.getSourceService())
                        .build())
                .stockDetails(StockDetails.builder()
                        .quantity(entity.getQuantity())
                        .originalQuantity(entity.getOriginalQuantity())
                        .unitPrice(entity.getUnitPrice())
                        .costPrice(entity.getCostPrice())
                        .entryDate(entity.getEntryDate())
                        .expirationDate(entity.getExpirationDate())
                        .batchNumber(entity.getBatchNumber())
                        .supplierInfo(entity.getSupplierId() != null ?
                                SupplierInfo.builder()
                                        .supplierId(entity.getSupplierId())
                                        .supplierName(entity.getSupplierName())
                                        .build() : null)
                        .build())
                .status(entity.getStatus())
                .metadata(StockMetadata.builder()
                        .createdAt(entity.getCreatedAtAsLocalDateTime())
                        .updatedAt(entity.getUpdatedAtAsLocalDateTime())
                        .createdBy(entity.getCreatedBy())
                        .build())
                .build();
    }

    public void updateEntityFromRequest(StockEntry entity, CreateStockEntryRequest request) {
        if (request.getQuantity() != null) {
            entity.setQuantity(request.getQuantity());
        }

        if (request.getUnitPrice() != null) {
            entity.setUnitPrice(request.getUnitPrice());
        }

        if (request.getCostPrice() != null) {
            entity.setCostPrice(request.getCostPrice());
        }

        if (request.getExpirationDate() != null) {
            entity.setExpirationDate(request.getExpirationDate());
        }

        if (request.getBatchNumber() != null) {
            entity.setBatchNumber(request.getBatchNumber());
        }

        if (request.getSupplierInfo() != null) {
            entity.setSupplierId(request.getSupplierInfo().getSupplierId());
            entity.setSupplierName(request.getSupplierInfo().getSupplierName());
        }
    }
}