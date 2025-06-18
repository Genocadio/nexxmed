package com.nexxserve.inventoryservice.dto;

import lombok.Data;

@Data
public class GenericMedicineDetails {
    private String chemicalName;
    private String classId;
    private String className;
    private String description;
    private boolean isParent;
}
