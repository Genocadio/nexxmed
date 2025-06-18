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
@Table(name = "visits")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Visit {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @NotNull(message = "Visit type is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VisitType visitType;

    @NotNull(message = "Department is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ServiceType department;

    @NotNull(message = "Priority is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PriorityLevel priority;

//    @NotNull(message = "Appointment time is required")
//    @Future(message = "Appointment time must be in the future")
//    @Column(nullable = false)
//    private LocalDateTime appointmentTime;

//    @NotBlank(message = "Chief complaint is required")
    @Column( columnDefinition = "TEXT")
    private String chiefComplaint;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "selected_insurance_id")
    private Insurance selectedInsurance;

    @Column(nullable = false)
    private boolean insuranceVerified;

    @NotNull(message = "Visit status is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VisitStatus status;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
