package com.nexxserve.medicalemr.controller;

import com.nexxserve.medicalemr.dto.PatientTransferRequestDto;
import com.nexxserve.medicalemr.entity.PatientTransfer;
import com.nexxserve.medicalemr.enums.ServiceType;
import com.nexxserve.medicalemr.enums.TransferStatus;
import com.nexxserve.medicalemr.service.PatientTransferService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transfers")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PatientTransferController {

    private final PatientTransferService transferService;

    @PostMapping
    public ResponseEntity<PatientTransfer> createTransfer(@Valid @RequestBody PatientTransferRequestDto transferDto) {
        PatientTransfer createdTransfer = transferService.createTransfer(transferDto);
        return new ResponseEntity<>(createdTransfer, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PatientTransfer> getTransferById(@PathVariable String id) {
        PatientTransfer transfer = transferService.getTransferById(id);
        return ResponseEntity.ok(transfer);
    }

    @GetMapping
    public ResponseEntity<Page<PatientTransfer>> getAllTransfers(Pageable pageable) {
        Page<PatientTransfer> transfers = transferService.getAllTransfers(pageable);
        return ResponseEntity.ok(transfers);
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<PatientTransfer>> getTransfersByPatient(@PathVariable String patientId) {
        List<PatientTransfer> transfers = transferService.getTransfersByPatient(patientId);
        return ResponseEntity.ok(transfers);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<PatientTransfer>> getTransfersByStatus(@PathVariable TransferStatus status) {
        List<PatientTransfer> transfers = transferService.getTransfersByStatus(status);
        return ResponseEntity.ok(transfers);
    }

    @GetMapping("/services")
    public ResponseEntity<List<PatientTransfer>> getTransfersByService(
            @RequestParam ServiceType fromService,
            @RequestParam ServiceType toService) {
        List<PatientTransfer> transfers = transferService.getTransfersByService(fromService, toService);
        return ResponseEntity.ok(transfers);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<PatientTransfer> updateTransferStatus(@PathVariable String id,
                                                                @RequestParam TransferStatus status) {
        PatientTransfer updatedTransfer = transferService.updateTransferStatus(id, status);
        return ResponseEntity.ok(updatedTransfer);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransfer(@PathVariable String id) {
        transferService.deleteTransfer(id);
        return ResponseEntity.noContent().build();
    }
}