package com.nexxserve.inventoryservice.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class SaleTransactionResponse {
    private UUID id;
    private LocalDateTime transactionDate;
    private String patientName;
    private String patientContact;
    private String prescriberName;
    private String prescriberLicenseNumber;
    private BigDecimal totalPrice;
    private BigDecimal totalInsurancePayment;
    private BigDecimal totalPatientPayment;
    private String paymentMode;
    private List<SaleItemResponse> items;

    @Data
    @Builder
    public static class SaleItemResponse {
        private UUID id;
        private UUID stockEntryId;
        private String productName;
        private Integer quantitySold;
        private BigDecimal unitPrice;
        private BigDecimal totalAmount;
        private String salesnotes;
        private Integer insuranceCoverage;
        private String insuranceName;
        private BigDecimal insurancePayment;
        private BigDecimal patientPayment;
    }
}