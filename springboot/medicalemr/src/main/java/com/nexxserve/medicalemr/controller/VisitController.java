package com.nexxserve.medicalemr.controller;

import com.nexxserve.medicalemr.dto.VisitRequestDto;
import com.nexxserve.medicalemr.entity.Visit;
import com.nexxserve.medicalemr.enums.ServiceType;
import com.nexxserve.medicalemr.enums.VisitStatus;
import com.nexxserve.medicalemr.service.VisitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/visits")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class VisitController {

    private final VisitService visitService;

    @PostMapping
    public ResponseEntity<Visit> createVisit(@Valid @RequestBody VisitRequestDto visitDto) {
        Visit createdVisit = visitService.createVisit(visitDto);
        return new ResponseEntity<>(createdVisit, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Visit> getVisitById(@PathVariable String id) {
        Visit visit = visitService.getVisitById(id);
        return ResponseEntity.ok(visit);
    }

    @GetMapping
    public ResponseEntity<Page<Visit>> getAllVisits(Pageable pageable) {
        Page<Visit> visits = visitService.getAllVisits(pageable);
        return ResponseEntity.ok(visits);
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<Visit>> getVisitsByPatient(@PathVariable String patientId) {
        List<Visit> visits = visitService.getVisitsByPatient(patientId);
        return ResponseEntity.ok(visits);
    }

    @GetMapping("/department/{department}")
    public ResponseEntity<List<Visit>> getVisitsByDepartment(@PathVariable ServiceType department) {
        List<Visit> visits = visitService.getVisitsByDepartment(department);
        return ResponseEntity.ok(visits);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Visit>> getVisitsByStatus(@PathVariable VisitStatus status) {
        List<Visit> visits = visitService.getVisitsByStatus(status);
        return ResponseEntity.ok(visits);
    }

    @GetMapping("/department/{department}/status/{status}")
    public ResponseEntity<List<Visit>> getVisitsByDepartmentAndStatus(
            @PathVariable ServiceType department,
            @PathVariable VisitStatus status) {
        List<Visit> visits = visitService.getVisitsByDepartmentAndStatus(department, status);
        return ResponseEntity.ok(visits);
    }

    @GetMapping("/time-range")
    public ResponseEntity<List<Visit>> getVisitsByTimeRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        List<Visit> visits = visitService.getVisitsByTimeRange(startTime, endTime);
        return ResponseEntity.ok(visits);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Visit> updateVisitStatus(@PathVariable String id,
                                                   @RequestParam VisitStatus status) {
        Visit updatedVisit = visitService.updateVisitStatus(id, status);
        return ResponseEntity.ok(updatedVisit);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Visit> updateVisit(@PathVariable String id,
                                             @Valid @RequestBody VisitRequestDto visitDto) {
        Visit updatedVisit = visitService.updateVisit(id, visitDto);
        return ResponseEntity.ok(updatedVisit);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVisit(@PathVariable String id) {
        visitService.deleteVisit(id);
        return ResponseEntity.noContent().build();
    }
}