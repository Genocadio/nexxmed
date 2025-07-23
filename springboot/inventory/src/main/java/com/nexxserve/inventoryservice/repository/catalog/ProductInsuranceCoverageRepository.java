package com.nexxserve.inventoryservice.repository.catalog;

import com.nexxserve.inventoryservice.enums.CoverageStatus;
import com.nexxserve.inventoryservice.entity.catalog.ProductInsuranceCoverage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductInsuranceCoverageRepository extends JpaRepository<ProductInsuranceCoverage, UUID> {

    @Query("SELECT c FROM ProductInsuranceCoverage c WHERE c.updatedAt > :lastSync")
    List<ProductInsuranceCoverage> findUpdatedAfter(@Param("lastSync") Instant lastSync);

    @Query("SELECT c FROM ProductInsuranceCoverage c WHERE c.createdAt > :lastSync")
    List<ProductInsuranceCoverage> findCreatedAfter(@Param("lastSync") Instant lastSync);

    List<ProductInsuranceCoverage> findByInsuranceId(UUID insuranceId);

    List<ProductInsuranceCoverage> findByProductFamilyId(UUID productFamilyId);

    List<ProductInsuranceCoverage> findByProductVariantId(UUID productVariantId);

    Optional<ProductInsuranceCoverage> findByInsuranceIdAndProductFamilyId(UUID insuranceId, UUID productFamilyId);

    Optional<ProductInsuranceCoverage> findByInsuranceIdAndProductVariantId(UUID insuranceId, UUID productVariantId);

    @Query("SELECT c FROM ProductInsuranceCoverage c WHERE c.syncVersion > :lastSyncVersion ORDER BY c.syncVersion ASC")
    Page<ProductInsuranceCoverage> findBySyncVersionGreaterThan(
            @Param("lastSyncVersion") Double lastSyncVersion, Pageable pageable);
}