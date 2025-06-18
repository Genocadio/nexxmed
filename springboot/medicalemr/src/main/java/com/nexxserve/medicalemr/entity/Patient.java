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
import java.util.List;

@Entity
@Table(name = "patients")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @NotBlank(message = "First name is required")
    @Column(nullable = false)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Column(nullable = false)
    private String lastName;

    @NotNull(message = "Date of birth is required")
    @Column(nullable = false)
    private LocalDate dateOfBirth;

    @NotNull(message = "Gender is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @NotBlank(message = "National ID is required")
    @Column(nullable = false, unique = true)
    private String nationalId;

    private String phone;

    @Email(message = "Invalid email format")
    private String email;

    private String address;

    private String nextOfKinName;

    private String nextOfKinPhone;

    @Enumerated(EnumType.STRING)
    private NextOfKinRelation nextOfKinRelation;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Insurance> insurances;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Visit> visits;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_visit_id")
    private Visit currentVisit;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_transfer_id")
    private PatientTransfer currentTransfer;

    @Column(unique = true)
    private String patientIdentifier;

    private LocalDateTime currentStatusUpdatedAt;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}