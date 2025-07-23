package com.nexxserve.inventoryservice.dto.medicine;

import lombok.Data;

@Data
public class MedicineInsuranceCoverage {
    private String id;
    private String insuranceName;
    private String coverageType;
    private double coveragePercentage;
    private double maxAmount;
    private String notes;
}
