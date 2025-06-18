package com.nexxserve.medicalemr.dto;

import com.nexxserve.medicalemr.enums.PriorityLevel;
import com.nexxserve.medicalemr.enums.ServiceType;
import com.nexxserve.medicalemr.enums.VisitStatus;
import com.nexxserve.medicalemr.enums.VisitType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VisitResponseDto {
    private String id;
    private VisitType visitType;
    private ServiceType department;
    private PriorityLevel priority;
    private String chiefComplaint;
    private String notes;
    private InsuranceResponseDto selectedInsurance;
    private boolean insuranceVerified;
    private VisitStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}