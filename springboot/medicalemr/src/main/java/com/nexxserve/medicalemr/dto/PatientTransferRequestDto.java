package com.nexxserve.medicalemr.dto;

import com.nexxserve.medicalemr.enums.*;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class PatientTransferRequestDto {

    @NotBlank(message = "Patient ID is required")
    private String patientId;

    @NotBlank(message = "Visit ID is required")
    private String visitId;

    @NotNull(message = "From service is required")
    private ServiceType fromService;

    @NotNull(message = "To service is required")
    private ServiceType toService;

    @NotNull(message = "Priority is required")
    private PriorityLevel priority;

    private String notes;
}