package com.nexxserve.medicalemr.dto;

import com.nexxserve.medicalemr.enums.PriorityLevel;
import com.nexxserve.medicalemr.enums.ServiceType;
import com.nexxserve.medicalemr.enums.TransferStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientTransferResponseDto {
    private String id;
    private VisitResponseDto visit;
    private ServiceType fromService;
    private ServiceType toService;
    private PriorityLevel priority;
    private String notes;
    private TransferStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}