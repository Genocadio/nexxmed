package com.nexxserve.billing.repository;

import com.nexxserve.billing.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    List<Invoice> findByPatientId(String patientId);
    List<Invoice> findByStatus(Invoice.InvoiceStatus status);
    List<Invoice> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT i FROM Invoice i WHERE i.patientId = :patientId AND i.status = :status")
    List<Invoice> findByPatientIdAndStatus(@Param("patientId") String patientId,
                                          @Param("status") Invoice.InvoiceStatus status);
}