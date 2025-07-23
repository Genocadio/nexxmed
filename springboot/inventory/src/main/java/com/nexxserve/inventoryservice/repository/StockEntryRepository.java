package com.nexxserve.inventoryservice.repository;

import com.nexxserve.inventoryservice.entity.inventory.StockEntry;
import com.nexxserve.inventoryservice.enums.ProductType;
import com.nexxserve.inventoryservice.enums.SourceService;
import com.nexxserve.inventoryservice.enums.StockStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StockEntryRepository extends JpaRepository<StockEntry, UUID> {

    Page<StockEntry> findByStatus(StockStatus status, Pageable pageable);

    Page<StockEntry> findByProductTypeAndSourceService(
            ProductType productType,
            SourceService sourceService,
            Pageable pageable);

    Optional<StockEntry> findByReferenceIdAndProductTypeAndSourceService(
            String referenceId,
            ProductType productType,
            SourceService sourceService);

    List<StockEntry> findByExpirationDateBetween(
            LocalDateTime start,
            LocalDateTime end);

    List<StockEntry> findByQuantityLessThanAndStatus(
            Integer threshold,
            StockStatus status);

    List<StockEntry> findBySupplierId(String supplierId);

    @Query("SELECT s FROM StockEntry s WHERE " +
            "LOWER(s.productName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<StockEntry> searchByProductName(@Param("searchTerm") String searchTerm, Pageable pageable);
}