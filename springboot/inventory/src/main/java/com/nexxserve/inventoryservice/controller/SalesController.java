package com.nexxserve.inventoryservice.controller;

import com.nexxserve.inventoryservice.dto.SaleTransactionRequest;
import com.nexxserve.inventoryservice.dto.SaleTransactionResponse;
import com.nexxserve.inventoryservice.service.SalesService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/sales")
@RequiredArgsConstructor
public class SalesController {


    private final SalesService salesService;

    @PostMapping
    public ResponseEntity<SaleTransactionResponse> createSale(@Valid @RequestBody SaleTransactionRequest request) {
        return new ResponseEntity<>(salesService.createSaleTransaction(request), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SaleTransactionResponse> getSale(@PathVariable UUID id) {
        return ResponseEntity.ok(salesService.getSaleTransaction(id));
    }

    @GetMapping
    public ResponseEntity<Page<SaleTransactionResponse>> getAllSales(Pageable pageable) {
        return ResponseEntity.ok(salesService.getAllSaleTransactions(pageable));
    }

    @GetMapping("/search/patient")
    public ResponseEntity<Page<SaleTransactionResponse>> searchByPatient(
            @RequestParam String name, Pageable pageable) {
        return ResponseEntity.ok(salesService.searchSalesByPatientName(name, pageable));
    }

    @GetMapping("/search/date")
    public ResponseEntity<Page<SaleTransactionResponse>> searchByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            Pageable pageable) {
        return ResponseEntity.ok(salesService.searchSalesByDateRange(from, to, pageable));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSale(@PathVariable UUID id) {
        salesService.deleteSaleTransaction(id);
        return ResponseEntity.noContent().build();
    }
}