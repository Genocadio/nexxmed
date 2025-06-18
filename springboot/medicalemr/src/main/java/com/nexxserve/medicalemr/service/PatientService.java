package com.nexxserve.medicalemr.service;

import com.nexxserve.medicalemr.dto.*;
import com.nexxserve.medicalemr.entity.Patient;
import com.nexxserve.medicalemr.entity.Insurance;
import com.nexxserve.medicalemr.entity.PatientTransfer;
import com.nexxserve.medicalemr.entity.Visit;
import com.nexxserve.medicalemr.enums.ServiceType;
import com.nexxserve.medicalemr.enums.PriorityLevel;
import com.nexxserve.medicalemr.enums.VisitType;
import com.nexxserve.medicalemr.repository.PatientRepository;
import com.nexxserve.medicalemr.repository.InsuranceRepository;
import com.nexxserve.medicalemr.exception.ResourceNotFoundException;
import com.nexxserve.medicalemr.exception.DuplicateResourceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.Math.log;

@Service
@Slf4j
@Transactional
public class PatientService {

    private final PatientRepository patientRepository;
    private final InsuranceRepository insuranceRepository;
    private final VisitService visitService;
    private final PatientIdentifierService patientIdentifierService;

    public PatientService(PatientRepository patientRepository,
                          InsuranceRepository insuranceRepository,
                          @Lazy PatientIdentifierService patientIdentifierService,
                          @Lazy VisitService visitService) {
        this.patientRepository = patientRepository;
        this.insuranceRepository = insuranceRepository;
        this.visitService = visitService;
        this.patientIdentifierService = patientIdentifierService;
    }

    public PatientResponseDto createPatient(PatientRequestDto patientDto) {
        PatientService.log.info("Creating new patient with national ID: {}", patientDto.getNationalId());

        // Check if national ID already exists
        if (patientRepository.existsByNationalId(patientDto.getNationalId())) {
            throw new DuplicateResourceException("Patient with national ID " + patientDto.getNationalId() + " already exists");
        }
        // Generate patient identifier
        String patientIdentifier = patientIdentifierService.generatePatientIdentifier(
                patientDto.getFirstName(),
                patientDto.getLastName()
        );

        Patient patient = Patient.builder()
                .firstName(patientDto.getFirstName())
                .lastName(patientDto.getLastName())
                .dateOfBirth(patientDto.getDateOfBirth())
                .gender(patientDto.getGender())
                .nationalId(patientDto.getNationalId())
                .patientIdentifier(patientIdentifier)
                .phone(patientDto.getPhone())
                .email(patientDto.getEmail())
                .address(patientDto.getAddress())
                .nextOfKinName(patientDto.getNextOfKinName())
                .nextOfKinPhone(patientDto.getNextOfKinPhone())
                .nextOfKinRelation(patientDto.getNextOfKinRelation())
                .notes(patientDto.getNotes())
                .build();

        Patient savedPatient = patientRepository.save(patient);

        // Create insurances if provided
        if (patientDto.getInsurances() != null && !patientDto.getInsurances().isEmpty()) {
            List<Insurance> insurances = patientDto.getInsurances().stream()
                    .map(insuranceDto -> createInsurance(savedPatient, insuranceDto))
                    .collect(Collectors.toList());

            insuranceRepository.saveAll(insurances);
        }
        createInitialVisit(savedPatient, patientDto.getRouteTo(), patientDto.getPriority());

        PatientService.log.info("Patient created successfully with ID: {}", savedPatient.getId());
        return convertToResponseDto(savedPatient);
    }

    private void createInitialVisit(Patient patient, ServiceType department, PriorityLevel priority) {
        PatientService.log.info("Creating initial visit for patient ID: {} in department: {}", patient.getId(), department);

        VisitRequestDto visitRequestDto = new VisitRequestDto();
        visitRequestDto.setPatientId(patient.getId());
        visitRequestDto.setVisitType(VisitType.CONSULTATION);
        visitRequestDto.setDepartment(department);
        visitRequestDto.setPriority(priority);
        visitRequestDto.setAppointmentTime(LocalDateTime.now().plusDays(1).withHour(9).withMinute(0));

        Visit initialVisit = visitService.createVisit(visitRequestDto);
        PatientService.log.info("Initial visit created with ID: {}", initialVisit.getId());
    }

    private Insurance createInsurance(Patient patient, InsuranceRequestDto insuranceDto) {
        return Insurance.builder()
                .patient(patient)
                .provider(insuranceDto.getProvider())
                .policyNumber(insuranceDto.getPolicyNumber())
                .policyExpiry(insuranceDto.getPolicyExpiry())
                .isPrimary(insuranceDto.isPrimary())
                .coverageType(insuranceDto.getCoverageType())
                .coveragePercentage(insuranceDto.getCoveragePercentage())
                .build();
    }

    @Transactional(readOnly = true)
    public PatientResponseDto getPatientDtoById(String id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with ID: " + id));
        return convertToResponseDto(patient);
    }

    @Transactional(readOnly = true)
    public Patient getPatientById(String id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with ID: " + id));
    }

    @Transactional(readOnly = true)
    public PatientResponseDto getPatientDtoByNationalId(String nationalId) {
        Patient patient = patientRepository.findByNationalId(nationalId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with National ID: " + nationalId));
        return convertToResponseDto(patient);
    }

    @Transactional(readOnly = true)
    public Page<PatientResponseDto> getAllPatients(Pageable pageable) {
        Page<Patient> patientsPage = patientRepository.findAll(pageable);
        log.info("Fetching all patients, total records: {}", patientsPage.getTotalElements());
        List<PatientResponseDto> patientDtos = patientsPage.getContent().stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());

        return new PageImpl<>(patientDtos, pageable, patientsPage.getTotalElements());
    }

    @Transactional(readOnly = true)
    public List<PatientResponseDto> searchPatients(String searchTerm) {
        List<Patient> patients = patientRepository.searchPatients(searchTerm);
        return patients.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    public PatientResponseDto updatePatient(String id, PatientRequestDto patientDto) {
        PatientService.log.info("Updating patient with ID: {}", id);

        Patient existingPatient = getPatientById(id);

        // Check if national ID is being changed and if it conflicts with another patient
        if (!existingPatient.getNationalId().equals(patientDto.getNationalId()) &&
                patientRepository.existsByNationalId(patientDto.getNationalId())) {
            throw new DuplicateResourceException("Patient with national ID " + patientDto.getNationalId() + " already exists");
        }

        // Update patient fields
        existingPatient.setFirstName(patientDto.getFirstName());
        existingPatient.setLastName(patientDto.getLastName());
        existingPatient.setDateOfBirth(patientDto.getDateOfBirth());
        existingPatient.setGender(patientDto.getGender());
        existingPatient.setNationalId(patientDto.getNationalId());
        existingPatient.setPhone(patientDto.getPhone());
        existingPatient.setEmail(patientDto.getEmail());
        existingPatient.setAddress(patientDto.getAddress());
        existingPatient.setNextOfKinName(patientDto.getNextOfKinName());
        existingPatient.setNextOfKinPhone(patientDto.getNextOfKinPhone());
        existingPatient.setNextOfKinRelation(patientDto.getNextOfKinRelation());
        existingPatient.setNotes(patientDto.getNotes());

        Patient updatedPatient = patientRepository.save(existingPatient);
        PatientService.log.info("Patient updated successfully with ID: {}", updatedPatient.getId());
        return convertToResponseDto(updatedPatient);
    }

    public void deletePatient(String id) {
        PatientService.log.info("Deleting patient with ID: {}", id);
        Patient patient = getPatientById(id);
        patientRepository.delete(patient);
        PatientService.log.info("Patient deleted successfully with ID: {}", id);
    }

    private PatientResponseDto convertToResponseDto(Patient patient) {
        PatientResponseDto dto = PatientResponseDto.builder()
                .id(patient.getId())
                .firstName(patient.getFirstName())
                .lastName(patient.getLastName())
                .dateOfBirth(patient.getDateOfBirth())
                .patientIdentifier(patient.getPatientIdentifier())
                .gender(patient.getGender())
                .nationalId(patient.getNationalId())
                .phone(patient.getPhone())
                .email(patient.getEmail())
                .address(patient.getAddress())
                .nextOfKinName(patient.getNextOfKinName())
                .nextOfKinPhone(patient.getNextOfKinPhone())
                .nextOfKinRelation(patient.getNextOfKinRelation())
                .notes(patient.getNotes())
                .createdAt(patient.getCreatedAt())
                .updatedAt(patient.getUpdatedAt())
                .currentStatusUpdatedAt(patient.getCurrentStatusUpdatedAt())
                .build();

        // Map insurances if available
        if (patient.getInsurances() != null) {
            dto.setInsurances(patient.getInsurances().stream()
                    .map(this::convertInsuranceToDto)
                    .collect(Collectors.toList()));
        } else {
            patient.setInsurances(null);
        }

        // Map visits if available
        if (patient.getVisits() != null) {
            dto.setVisits(patient.getVisits().stream()
                    .map(this::convertVisitToDto)
                    .collect(Collectors.toList()));
        } else {
            patient.setVisits(null);
        }

        // Map current visit if available
        if (patient.getCurrentVisit() != null) {
            dto.setCurrentVisit(convertVisitToDto(patient.getCurrentVisit()));
        } else {
            patient.setCurrentVisit(null);
        }

        // Map current transfer if available
        if (patient.getCurrentTransfer() != null) {
            dto.setCurrentTransfer(convertTransferToDto(patient.getCurrentTransfer()));
        } else {
            patient.setCurrentTransfer(null);
        }

        return dto;
    }

    private InsuranceResponseDto convertInsuranceToDto(Insurance insurance) {
        return InsuranceResponseDto.builder()
                .id(insurance.getId())
                .provider(insurance.getProvider())
                .policyNumber(insurance.getPolicyNumber())
                .policyExpiry(insurance.getPolicyExpiry())
                .isPrimary(insurance.isPrimary())
                .coverageType(insurance.getCoverageType())
                .coveragePercentage(insurance.getCoveragePercentage())
                .createdAt(insurance.getCreatedAt())
                .updatedAt(insurance.getUpdatedAt())
                .build();
    }

    private VisitResponseDto convertVisitToDto(Visit visit) {
        VisitResponseDto dto = VisitResponseDto.builder()
                .id(visit.getId())
                .visitType(visit.getVisitType())
                .department(visit.getDepartment())
                .priority(visit.getPriority())
                .chiefComplaint(visit.getChiefComplaint())
                .notes(visit.getNotes())
                .insuranceVerified(visit.isInsuranceVerified())
                .status(visit.getStatus())
                .createdAt(visit.getCreatedAt())
                .updatedAt(visit.getUpdatedAt())
                .build();

        if (visit.getSelectedInsurance() != null) {
            dto.setSelectedInsurance(convertInsuranceToDto(visit.getSelectedInsurance()));
        }

        return dto;
    }

    private PatientTransferResponseDto convertTransferToDto(PatientTransfer transfer) {
        return PatientTransferResponseDto.builder()
                .id(transfer.getId())
                .visit(convertVisitToDto(transfer.getVisit()))
                .fromService(transfer.getFromService())
                .toService(transfer.getToService())
                .priority(transfer.getPriority())
                .notes(transfer.getNotes())
                .status(transfer.getStatus())
                .createdAt(transfer.getCreatedAt())
                .updatedAt(transfer.getUpdatedAt())
                .build();
    }
}