package com.nexxserve.medicalemr.dto;

import com.nexxserve.medicalemr.enums.Gender;
import com.nexxserve.medicalemr.enums.NextOfKinRelation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientResponseDto {
    private String id;
    private String patientIdentifier;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private Gender gender;
    private String nationalId;
    private String phone;
    private String email;
    private String address;
    private String nextOfKinName;
    private String nextOfKinPhone;
    private NextOfKinRelation nextOfKinRelation;
    private List<InsuranceResponseDto> insurances;
    private List<VisitResponseDto> visits;
    private String notes;
    private VisitResponseDto currentVisit;
    private PatientTransferResponseDto currentTransfer;
    private LocalDateTime currentStatusUpdatedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}