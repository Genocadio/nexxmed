package com.nexxserve.medicalemr.service;

import com.nexxserve.medicalemr.dto.VisitRequestDto;
import com.nexxserve.medicalemr.entity.Visit;
import com.nexxserve.medicalemr.entity.Patient;
import com.nexxserve.medicalemr.entity.Insurance;
import com.nexxserve.medicalemr.enums.ServiceType;
import com.nexxserve.medicalemr.enums.VisitStatus;
import com.nexxserve.medicalemr.repository.PatientRepository;
import com.nexxserve.medicalemr.repository.VisitRepository;
import com.nexxserve.medicalemr.repository.InsuranceRepository;
import com.nexxserve.medicalemr.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class VisitService {

    private final VisitRepository visitRepository;
    private final PatientService patientService;
    private final InsuranceRepository insuranceRepository;
    private final PatientRepository patientRepository;

    public Visit createVisit(VisitRequestDto visitDto) {
        log.info("Creating new visit for patient ID: {}", visitDto.getPatientId());

        Patient patient = patientService.getPatientById(visitDto.getPatientId());

        Insurance selectedInsurance = null;
        if (visitDto.getSelectedInsuranceId() != null) {
            selectedInsurance = insuranceRepository.findById(visitDto.getSelectedInsuranceId())
                    .orElseThrow(() -> new ResourceNotFoundException("Insurance not found with ID: " + visitDto.getSelectedInsuranceId()));
        }

        Visit visit = Visit.builder()
                .patient(patient)
                .visitType(visitDto.getVisitType())
                .department(visitDto.getDepartment())
                .priority(visitDto.getPriority())
                .chiefComplaint(visitDto.getChiefComplaint())
                .notes(visitDto.getNotes())
                .selectedInsurance(selectedInsurance)
                .insuranceVerified(visitDto.isInsuranceVerified())
                .status(VisitStatus.SCHEDULED)
                .build();

        Visit savedVisit = visitRepository.save(visit);
        patient.setCurrentVisit(visit);
        patient.setCurrentTransfer(null);
        patient.setCurrentStatusUpdatedAt(LocalDateTime.now());
        patientRepository.save(patient);
        log.info("Visit created successfully with ID: {}", savedVisit.getId());
        return savedVisit;
    }

    @Transactional(readOnly = true)
    public Visit getVisitById(String id) {
        return visitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Visit not found with ID: " + id));
    }

    @Transactional(readOnly = true)
    public Page<Visit> getAllVisits(Pageable pageable) {
        return visitRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public List<Visit> getVisitsByPatient(String patientId) {
        Patient patient = patientService.getPatientById(patientId);
        return visitRepository.findPatientVisitsOrderByDate(patient);
    }

    @Transactional(readOnly = true)
    public List<Visit> getVisitsByDepartment(ServiceType department) {
        return visitRepository.findByDepartment(department);
    }

    @Transactional(readOnly = true)
    public List<Visit> getVisitsByStatus(VisitStatus status) {
        return visitRepository.findByStatus(status);
    }

    @Transactional(readOnly = true)
    public List<Visit> getVisitsByDepartmentAndStatus(ServiceType department, VisitStatus status) {
        return visitRepository.findByDepartmentAndStatus(department, status);
    }

    @Transactional(readOnly = true)
    public List<Visit> getVisitsByTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        return visitRepository.findVisitsByTimeRange(startTime, endTime);
    }

    public Visit updateVisitStatus(String id, VisitStatus status) {
        log.info("Updating visit status for ID: {} to {}", id, status);
        Visit visit = getVisitById(id);
        visit.setStatus(status);
        Visit updatedVisit = visitRepository.save(visit);
        log.info("Visit status updated successfully for ID: {}", id);
        return updatedVisit;
    }

    public Visit updateVisit(String id, VisitRequestDto visitDto) {
        log.info("Updating visit with ID: {}", id);

        Visit existingVisit = getVisitById(id);

        Insurance selectedInsurance = null;
        if (visitDto.getSelectedInsuranceId() != null) {
            selectedInsurance = insuranceRepository.findById(visitDto.getSelectedInsuranceId())
                    .orElseThrow(() -> new ResourceNotFoundException("Insurance not found with ID: " + visitDto.getSelectedInsuranceId()));
        }

        existingVisit.setVisitType(visitDto.getVisitType());
        existingVisit.setDepartment(visitDto.getDepartment());
        existingVisit.setPriority(visitDto.getPriority());
        existingVisit.setChiefComplaint(visitDto.getChiefComplaint());
        existingVisit.setNotes(visitDto.getNotes());
        existingVisit.setSelectedInsurance(selectedInsurance);
        existingVisit.setInsuranceVerified(visitDto.isInsuranceVerified());

        Visit updatedVisit = visitRepository.save(existingVisit);
        log.info("Visit updated successfully with ID: {}", updatedVisit.getId());
        return updatedVisit;
    }

    public void deleteVisit(String id) {
        log.info("Deleting visit with ID: {}", id);
        Visit visit = getVisitById(id);
        visitRepository.delete(visit);
        log.info("Visit deleted successfully with ID: {}", id);
    }
}