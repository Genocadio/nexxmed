package com.nexxserve.inventoryservice.controller;

import com.nexxserve.inventoryservice.dto.stock.CreateStockEntryRequest;
import com.nexxserve.inventoryservice.dto.stock.StockAlertResponse;
import com.nexxserve.inventoryservice.dto.stock.StockEntryResponse;
import com.nexxserve.inventoryservice.enums.AlertType;
import com.nexxserve.inventoryservice.enums.ProductType;
import com.nexxserve.inventoryservice.enums.SourceService;
import com.nexxserve.inventoryservice.service.StockService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    @GetMapping("/search")
    public ResponseEntity<Page<StockEntryResponse>> searchStocksByProductName(
            @RequestParam String name,
            Pageable pageable) {
        return ResponseEntity.ok(stockService.searchStocksByProductName(name, pageable));
    }

    @PostMapping
    public ResponseEntity<StockEntryResponse> createStock(@Valid @RequestBody CreateStockEntryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(stockService.createStock(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<StockEntryResponse> getStockById(@PathVariable UUID id) {
        return ResponseEntity.ok(stockService.getStockById(id));
    }

    @GetMapping
    public ResponseEntity<Page<StockEntryResponse>> getAllStocks(Pageable pageable) {
        return ResponseEntity.ok(stockService.getAllStocks(pageable));
    }

    @GetMapping("/filter")
    public ResponseEntity<Page<StockEntryResponse>> getStocksByType(
            @RequestParam ProductType productType,
            @RequestParam SourceService sourceService,
            Pageable pageable) {
        return ResponseEntity.ok(stockService.getStocksByProductTypeAndSourceService(productType, sourceService, pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<StockEntryResponse> updateStock(
            @PathVariable UUID id,
            @Valid @RequestBody CreateStockEntryRequest request) {
        return ResponseEntity.ok(stockService.updateStock(id, request));
    }

    @PatchMapping("/{id}/quantity")
    public ResponseEntity<StockEntryResponse> adjustStockQuantity(
            @PathVariable UUID id,
            @RequestBody Map<String, Integer> quantityRequest) {
        Integer quantityChange = quantityRequest.get("quantityChange");
        if (quantityChange == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(stockService.adjustStockQuantity(id, quantityChange));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStock(@PathVariable UUID id) {
        stockService.deleteStock(id);
        return ResponseEntity.noContent().build();
    }

    // Alert related endpoints

    @GetMapping("/{id}/alerts")
    public ResponseEntity<List<StockAlertResponse>> getAlertsByStockId(@PathVariable UUID id) {
        return ResponseEntity.ok(stockService.getAlertsByStockId(id));
    }

    @GetMapping("/alerts")
    public ResponseEntity<Page<StockAlertResponse>> getAllActiveAlerts(Pageable pageable) {
        return ResponseEntity.ok(stockService.getAllActiveAlerts(pageable));
    }

    @GetMapping("/alerts/type/{alertType}")
    public ResponseEntity<List<StockAlertResponse>> getAlertsByType(@PathVariable AlertType alertType) {
        return ResponseEntity.ok(stockService.getAlertsByType(alertType));
    }

    @PatchMapping("/alerts/{alertId}/resolve")
    public ResponseEntity<StockAlertResponse> resolveAlert(@PathVariable UUID alertId) {
        return ResponseEntity.ok(stockService.resolveAlert(alertId));
    }
}