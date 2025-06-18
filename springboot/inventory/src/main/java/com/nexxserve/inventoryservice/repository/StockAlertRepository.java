package com.nexxserve.inventoryservice.repository;

import com.nexxserve.inventoryservice.entity.StockAlert;
import com.nexxserve.inventoryservice.entity.StockEntry;
import com.nexxserve.inventoryservice.enums.AlertStatus;
import com.nexxserve.inventoryservice.enums.AlertType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface StockAlertRepository extends JpaRepository<StockAlert, UUID> {

    List<StockAlert> findByStockEntry(StockEntry stockEntry);

    List<StockAlert> findByAlertType(AlertType alertType);

    Page<StockAlert> findByStatus(AlertStatus status, Pageable pageable);

    List<StockAlert> findByAlertDateBetween(
            LocalDateTime start,
            LocalDateTime end);

    List<StockAlert> findByStockEntryAndAlertTypeAndStatus(
            StockEntry stockEntry,
            AlertType alertType,
            AlertStatus status);
}