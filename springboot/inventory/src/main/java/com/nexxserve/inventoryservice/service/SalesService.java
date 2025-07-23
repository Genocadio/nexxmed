package com.nexxserve.inventoryservice.service;

import com.nexxserve.inventoryservice.dto.SaleTransactionRequest;
import com.nexxserve.inventoryservice.dto.SaleTransactionResponse;
import com.nexxserve.inventoryservice.entity.inventory.SaleItem;
import com.nexxserve.inventoryservice.entity.inventory.SaleTransaction;
import com.nexxserve.inventoryservice.entity.inventory.StockEntry;
import com.nexxserve.inventoryservice.exception.InsufficientStockException;
import com.nexxserve.inventoryservice.exception.ResourceNotFoundException;
import com.nexxserve.inventoryservice.repository.SaleTransactionRepository;
import com.nexxserve.inventoryservice.repository.StockEntryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SalesService {

    private final SaleTransactionRepository saleTransactionRepository;
    private final StockEntryRepository stockEntryRepository;
    private final UserService userService;

    @Transactional
    public SaleTransactionResponse createSaleTransaction(SaleTransactionRequest request) {
        // Validate and prepare stock entries
        List<StockEntry> stockEntriesToUpdate = new ArrayList<>();

        // Check if all stock items exist and have sufficient quantity
        for (SaleTransactionRequest.SaleItemDetail item : request.getItems()) {
            StockEntry stockEntry = stockEntryRepository.findById(item.getStockId())
                    .orElseThrow(() -> new ResourceNotFoundException("Stock entry not found with ID: " + item.getStockId()));

            if (stockEntry.getQuantity() < item.getQuantity()) {
                throw new InsufficientStockException("Insufficient stock for product: " + stockEntry.getProductName() +
                        ". Available: " + stockEntry.getQuantity() + ", Requested: " + item.getQuantity());
            }

            stockEntriesToUpdate.add(stockEntry);
        }

        // Create sale transaction
        SaleTransaction transaction = SaleTransaction.builder()
                .transactionDate(LocalDateTime.now())
                .patientName(request.getPatient().getName())
                .patientContact(request.getPatient().getContact())
                .totalPrice(request.getTotalPrice())
                .totalInsurancePayment(request.getTotalInsurancePayment())
                .totalPatientPayment(request.getTotalPatientPayment())
                .paymentMode(request.getPaymentMode())
                .notes(request.getNotes())
                .items(new ArrayList<>())
                .createdBy(userService.getCurrentUserId().toString())
                .build();

        // Add insurance details if present
        if (request.getPatient().getInsurance() != null) {
            String insuranceIdStr = request.getPatient().getInsurance().getId();
            if (!"custom".equals(insuranceIdStr)) {
                try {
                    transaction.setInsuranceId(UUID.fromString(insuranceIdStr));
                } catch (Exception e) {
                    log.warn("Invalid insurance ID: " + insuranceIdStr);
                    transaction.setInsuranceId(null); // Ignore if not a valid UUID
                }
            } else {
                log.warn("Insurance ID is custom.");
                transaction.setInsuranceId(null); // Custom insurance, no ID
            }

            transaction.setInsuranceName(request.getPatient().getInsurance().getName());
            transaction.setInsuranceCoverage(request.getPatient().getInsurance().getCoverage());
            transaction.setInsuranceCardNumber(request.getPatient().getInsurance().getCardNumber());
            transaction.setPrincipalMemberName(request.getPatient().getInsurance().getPrincipalMemberName());
        }

        // Add prescriber details
        transaction.setPrescriberName(request.getPrescriber().getName());
        transaction.setPrescriberOrganization(request.getPrescriber().getOrganization());
        transaction.setPrescriberContact(request.getPrescriber().getContact());
        transaction.setPrescriberType(request.getPrescriber().getType());
        transaction.setLicenseNumber(request.getPrescriber().getLicenseNumber());

        // Create sale items and update stock quantities
        for (int i = 0; i < request.getItems().size(); i++) {
            SaleTransactionRequest.SaleItemDetail itemRequest = request.getItems().get(i);
            StockEntry stockEntry = stockEntriesToUpdate.get(i);

            SaleItem saleItem = SaleItem.builder()
                    .stockEntry(stockEntry)
                    .quantitySold(itemRequest.getQuantity())
                    .Salesnotes(itemRequest.getSalesnotes())
                    .unitPrice(itemRequest.getUnitPrice())
                    .insuranceCoverage(itemRequest.getInsuranceCoverage())
                    .insurancePayment(itemRequest.getInsurancePayment())
                    .patientPayment(itemRequest.getPatientPayment())
                    .build();

            transaction.addItem(saleItem);

            // Reduce stock quantity
            stockEntry.reduceQuantity(itemRequest.getQuantity());
            stockEntryRepository.save(stockEntry);
        }

        // Save transaction
        SaleTransaction savedTransaction = saleTransactionRepository.save(transaction);

        // Map to response
        return mapToResponse(savedTransaction);
    }

    @Transactional(readOnly = true)
    public SaleTransactionResponse getSaleTransaction(UUID id) {
        SaleTransaction transaction = saleTransactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sale transaction not found with ID: " + id));

        return mapToResponse(transaction);
    }

    @Transactional(readOnly = true)
    public Page<SaleTransactionResponse> getAllSaleTransactions(Pageable pageable) {
        return saleTransactionRepository.findAll(pageable)
                .map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public Page<SaleTransactionResponse> searchSalesByPatientName(String patientName, Pageable pageable) {
        return saleTransactionRepository.findByPatientNameContainingIgnoreCase(patientName, pageable)
                .map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public Page<SaleTransactionResponse> searchSalesByDateRange(LocalDateTime from, LocalDateTime to, Pageable pageable) {
        return saleTransactionRepository.findByTransactionDateBetween(from, to, pageable)
                .map(this::mapToResponse);
    }

    @Transactional
    public void deleteSaleTransaction(UUID id) {
        SaleTransaction transaction = saleTransactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sale transaction not found with ID: " + id));

        // Return items to stock before deleting
        for (SaleItem item : transaction.getItems()) {
            StockEntry stockEntry = item.getStockEntry();
            stockEntry.increaseQuantity(item.getQuantitySold());
            stockEntryRepository.save(stockEntry);
        }

        saleTransactionRepository.delete(transaction);
    }

    private SaleTransactionResponse mapToResponse(SaleTransaction transaction) {
        List<SaleTransactionResponse.SaleItemResponse> itemResponses = transaction.getItems().stream()
                .map(item -> SaleTransactionResponse.SaleItemResponse.builder()
                        .id(item.getId())
                        .stockEntryId(item.getStockEntry().getId())
                        .productName(item.getStockEntry().getProductName())
                        .quantitySold(item.getQuantitySold())
                        .salesnotes(item.getSalesnotes())
                        .unitPrice(item.getUnitPrice())
                        .totalAmount(item.getTotalAmount())
                        .insuranceCoverage(item.getInsuranceCoverage())
                        .insuranceName(transaction.getInsuranceName())
                        .insurancePayment(item.getInsurancePayment())
                        .patientPayment(item.getPatientPayment())
                        .build())
                .collect(Collectors.toList());

        return SaleTransactionResponse.builder()
                .id(transaction.getId())
                .transactionDate(transaction.getTransactionDate())
                .patientName(transaction.getPatientName())
                .patientContact(transaction.getPatientContact())
                .prescriberName(transaction.getPrescriberName())
                .prescriberLicenseNumber(transaction.getLicenseNumber())
                .totalPrice(transaction.getTotalPrice())
                .totalInsurancePayment(transaction.getTotalInsurancePayment())
                .totalPatientPayment(transaction.getTotalPatientPayment())
                .paymentMode(transaction.getPaymentMode())
                .items(itemResponses)
                .build();
    }
}