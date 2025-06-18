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

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "insurances")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Insurance {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @NotNull(message = "Insurance provider is required")
    @Column(nullable = false)
    private String provider;

    @NotBlank(message = "Policy number is required")
    @Column(nullable = false)
    private String policyNumber;

    @NotNull(message = "Policy expiry date is required")
    @Future(message = "Policy expiry date must be in the future")
    @Column(nullable = false)
    private LocalDate policyExpiry;

    @Column(nullable = false)
    private boolean isPrimary;

    @NotNull(message = "Coverage type is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CoverageType coverageType;

    @NotNull(message = "Coverage percentage is required")
    @Column(nullable = false)
    private int coveragePercentage;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}