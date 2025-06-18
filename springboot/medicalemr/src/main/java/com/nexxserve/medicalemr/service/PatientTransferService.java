package com.nexxserve.medicalemr.service;

import com.nexxserve.medicalemr.dto.PatientTransferRequestDto;
import com.nexxserve.medicalemr.entity.PatientTransfer;
import com.nexxserve.medicalemr.entity.Patient;
import com.nexxserve.medicalemr.entity.Visit;
import com.nexxserve.medicalemr.enums.ServiceType;
import com.nexxserve.medicalemr.enums.TransferStatus;
import com.nexxserve.medicalemr.repository.PatientRepository;
import com.nexxserve.medicalemr.repository.PatientTransferRepository;
import com.nexxserve.medicalemr.exception.ResourceNotFoundException;
import com.nexxserve.medicalemr.enums.VisitStatus;
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
public class PatientTransferService {

    private final PatientTransferRepository transferRepository;
    private final PatientService patientService;
    private final VisitService visitService;
    private final PatientRepository patientRepository;

    public PatientTransfer createTransfer(PatientTransferRequestDto transferDto) {
        log.info("Creating new patient transfer for patient ID: {}", transferDto.getPatientId());

        Patient patient = patientService.getPatientById(transferDto.getPatientId());

        // Get linked visit and validate its status
        Visit visit = visitService.getVisitById(transferDto.getVisitId());
        if (visit.getStatus() == VisitStatus.COMPLETED || visit.getStatus() == VisitStatus.CANCELLED) {
            throw new IllegalStateException("Cannot create transfer for completed or cancelled visit");
        }

        PatientTransfer transfer = PatientTransfer.builder()
                .patient(patient)
                .fromService(transferDto.getFromService())
                .toService(transferDto.getToService())
                .priority(transferDto.getPriority())
                .notes(transferDto.getNotes())
                .status(TransferStatus.PENDING)
                .build();

        PatientTransfer savedTransfer = transferRepository.save(transfer);

        // Update patient's current transfer
        patient.setCurrentTransfer(savedTransfer);
        patient.setCurrentVisit(null);  // Clear current visit when on transfer
        patient.setCurrentStatusUpdatedAt(LocalDateTime.now());
        patientRepository.save(patient);

        log.info("Patient transfer created successfully with ID: {}", savedTransfer.getId());
        return savedTransfer;
    }

    @Transactional(readOnly = true)
    public PatientTransfer getTransferById(String id) {
        return transferRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient transfer not found with ID: " + id));
    }

    @Transactional(readOnly = true)
    public Page<PatientTransfer> getAllTransfers(Pageable pageable) {
        return transferRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public List<PatientTransfer> getTransfersByPatient(String patientId) {
        Patient patient = patientService.getPatientById(patientId);
        return transferRepository.findByPatient(patient);
    }

    @Transactional(readOnly = true)
    public List<PatientTransfer> getTransfersByStatus(TransferStatus status) {
        return transferRepository.findByStatus(status);
    }

    @Transactional(readOnly = true)
    public List<PatientTransfer> getTransfersByService(ServiceType fromService, ServiceType toService) {
        return transferRepository.findByServiceTransfer(fromService, toService);
    }

    public PatientTransfer updateTransferStatus(String id, TransferStatus status) {
        log.info("Updating transfer status for ID: {} to {}", id, status);
        PatientTransfer transfer = getTransferById(id);
        transfer.setStatus(status);

        // If transfer is completed, update patient's route
        if (status == TransferStatus.COMPLETED) {
            Patient patient = transfer.getPatient();
            Visit visit = transfer.getVisit();
            // Clear transfer reference and set visit as current
            patient.setCurrentTransfer(null);
            patient.setCurrentVisit(visit);
            patient.setCurrentStatusUpdatedAt(LocalDateTime.now());
            patientRepository.save(patient);
        }

        PatientTransfer updatedTransfer = transferRepository.save(transfer);
        log.info("Transfer status updated successfully for ID: {}", id);
        return updatedTransfer;
    }

    private com.nexxserve.medicalemr.dto.PatientRequestDto convertPatientToDto(Patient patient) {
        com.nexxserve.medicalemr.dto.PatientRequestDto dto = new com.nexxserve.medicalemr.dto.PatientRequestDto();
        dto.setFirstName(patient.getFirstName());
        dto.setLastName(patient.getLastName());
        dto.setDateOfBirth(patient.getDateOfBirth());
        dto.setGender(patient.getGender());
        dto.setNationalId(patient.getNationalId());
        dto.setPhone(patient.getPhone());
        dto.setEmail(patient.getEmail());
        dto.setAddress(patient.getAddress());
        dto.setNextOfKinName(patient.getNextOfKinName());
        dto.setNextOfKinPhone(patient.getNextOfKinPhone());
        dto.setNextOfKinRelation(patient.getNextOfKinRelation());
        dto.setNotes(patient.getNotes());
        return dto;
    }

    public void deleteTransfer(String id) {
        log.info("Deleting transfer with ID: {}", id);
        PatientTransfer transfer = getTransferById(id);
        transferRepository.delete(transfer);
        log.info("Transfer deleted successfully with ID: {}", id);
    }
}
