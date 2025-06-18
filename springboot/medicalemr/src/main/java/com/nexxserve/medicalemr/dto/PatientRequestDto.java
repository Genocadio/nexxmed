package com.nexxserve.medicalemr.dto;

import com.nexxserve.medicalemr.enums.*;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class PatientRequestDto {

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    @NotNull(message = "Gender is required")
    private Gender gender;

    @NotBlank(message = "National ID is required")
    private String nationalId;

//    @NotBlank(message = "Phone number is required")
//    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number format")
    private String phone;

    @Email(message = "Invalid email format")
    private String email;

//    @NotBlank(message = "Address is required")
    private String address;

//    @NotBlank(message = "Next of kin name is required")
    private String nextOfKinName;

//    @NotBlank(message = "Next of kin phone is required")
//    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number format")
    private String nextOfKinPhone;

//    @NotNull(message = "Next of kin relation is required")
    private NextOfKinRelation nextOfKinRelation;

    @NotNull(message = "Route to service is required")
    private ServiceType routeTo;

    @NotNull(message = "Priority is required")
    private PriorityLevel priority;

    private String notes;

    private List<InsuranceRequestDto> insurances;
}