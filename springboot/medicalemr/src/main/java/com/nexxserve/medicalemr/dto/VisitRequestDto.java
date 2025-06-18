package com.nexxserve.medicalemr.dto;

import com.nexxserve.medicalemr.enums.*;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class VisitRequestDto {

    @NotBlank(message = "Patient ID is required")
    private String patientId;

    @NotNull(message = "Visit type is required")
    private VisitType visitType;

    @NotNull(message = "Department is required")
    private ServiceType department;

    @NotNull(message = "Priority is required")
    private PriorityLevel priority;

    @NotNull(message = "Appointment time is required")
    @Future(message = "Appointment time must be in the future")
    private LocalDateTime appointmentTime;

//    @NotBlank(message = "Chief complaint is required")
    private String chiefComplaint;

    private String notes;

    private String selectedInsuranceId;

    private boolean insuranceVerified;
}