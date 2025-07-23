package com.nexxserve.inventoryservice.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class SaleTransactionRequest {
    @NotNull(message = "Patient information is required")
    @Valid
    private PatientInfo patient;

    @NotNull(message = "Prescriber information is required")
    @Valid
    private PrescriberInfo prescriber;

    @NotEmpty(message = "Sale items cannot be empty")
    @Valid
    private List<SaleItemDetail> items;

    @NotNull(message = "Total price is required")
    private BigDecimal totalPrice;

    private BigDecimal totalInsurancePayment;
    private BigDecimal totalPatientPayment;
    private String paymentMode;
    private String notes;

    @Data
    @Builder
    public static class PatientInfo {
        private String name;
        private String contact;
        private InsuranceInfo insurance;
    }

    @Data
    @Builder
    public static class InsuranceInfo {
        private String id;
        private String name;
        private Integer coverage;
        private String cardNumber;
        private String principalMemberName;
    }

    @Data
    @Builder
    public static class PrescriberInfo {
        private String name;
        private String organization;
        private String contact;
        private String licenseNumber;
        private String type;
    }

    @Data
    @Builder
    public static class SaleItemDetail {
        @NotNull(message = "Stock ID is required")
        private UUID stockId;

        @NotNull(message = "Quantity is required")
        private Integer quantity;

        private String salesnotes;

        private BigDecimal unitPrice;
        private Integer insuranceCoverage;
        private BigDecimal insurancePayment;
        private BigDecimal patientPayment;
        private String insuranceId;
        private String insuranceName;
    }
}