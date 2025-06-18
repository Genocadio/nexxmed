package com.nexxserve.medicalemr.controller;

import com.nexxserve.medicalemr.dto.PatientRequestDto;
import com.nexxserve.medicalemr.dto.PatientResponseDto;
import com.nexxserve.medicalemr.dto.PatientTransferResponseDto;
import com.nexxserve.medicalemr.entity.Patient;
import com.nexxserve.medicalemr.enums.ServiceType;
import com.nexxserve.medicalemr.enums.PriorityLevel;
import com.nexxserve.medicalemr.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PatientController {

    private final PatientService patientService;

    @PostMapping
    public ResponseEntity<PatientResponseDto> createPatient(@Valid @RequestBody PatientRequestDto patientDto) {
        PatientResponseDto createdPatient = patientService.createPatient(patientDto);
        return new ResponseEntity<>(createdPatient, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Patient> getPatientById(@PathVariable String id) {
        Patient patient = patientService.getPatientById(id);
        return ResponseEntity.ok(patient);
    }


    @GetMapping
    public ResponseEntity<Page<PatientResponseDto>> getAllPatients(Pageable pageable) {
        Page<PatientResponseDto> patients = patientService.getAllPatients(pageable);
        return ResponseEntity.ok(patients);
    }


    @GetMapping("/search")
    public ResponseEntity<List<PatientResponseDto>> searchPatients(@RequestParam String query) {
        List<PatientResponseDto> patients = patientService.searchPatients(query);
        return ResponseEntity.ok(patients);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PatientResponseDto> updatePatient(@PathVariable String id,
                                                 @Valid @RequestBody PatientRequestDto patientDto) {
        PatientResponseDto updatedPatient = patientService.updatePatient(id, patientDto);
        return ResponseEntity.ok(updatedPatient);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable String id) {
        patientService.deletePatient(id);
        return ResponseEntity.noContent().build();
    }
}