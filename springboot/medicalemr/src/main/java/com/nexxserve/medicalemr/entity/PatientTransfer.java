package com.nexxserve.medicalemr.entity;

import com.nexxserve.medicalemr.enums.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "patient_transfers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientTransfer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "visit_id", nullable = false)
    private Visit visit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @NotNull(message = "From service is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ServiceType fromService;

    @NotNull(message = "To service is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ServiceType toService;

    @NotNull(message = "Priority is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PriorityLevel priority;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @NotNull(message = "Transfer status is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransferStatus status;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}