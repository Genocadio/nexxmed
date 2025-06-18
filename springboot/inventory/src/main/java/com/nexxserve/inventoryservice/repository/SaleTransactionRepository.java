package com.nexxserve.inventoryservice.repository;

import com.nexxserve.inventoryservice.entity.SaleTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public interface SaleTransactionRepository extends JpaRepository<SaleTransaction, UUID> {
    Page<SaleTransaction> findByPatientNameContainingIgnoreCase(String patientName, Pageable pageable);
    Page<SaleTransaction> findByTransactionDateBetween(LocalDateTime from, LocalDateTime to, Pageable pageable);
}