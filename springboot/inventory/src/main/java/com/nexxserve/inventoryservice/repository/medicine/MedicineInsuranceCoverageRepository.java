package com.nexxserve.inventoryservice.repository.medicine;

import com.nexxserve.inventoryservice.entity.medicine.MedicineInsuranceCoverage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MedicineInsuranceCoverageRepository extends JpaRepository<MedicineInsuranceCoverage, UUID> {

    List<MedicineInsuranceCoverage> findByInsuranceId(UUID insuranceId);

    List<MedicineInsuranceCoverage> findByGenericId(UUID genericId);

    List<MedicineInsuranceCoverage> findByBrandId(UUID brandId);

    List<MedicineInsuranceCoverage> findByVariantId(UUID variantId);

    // Fix: reference the correct field paths in the entity
    @Query("SELECT mic FROM MedicineInsuranceCoverage mic WHERE mic.insurance.id = :insuranceId AND mic.generic.id = :genericId")
    Optional<MedicineInsuranceCoverage> findByInsuranceIdAndGenericId(
            @Param("insuranceId") UUID insuranceId,
            @Param("genericId") UUID genericId
    );

    @Query("SELECT mic FROM MedicineInsuranceCoverage mic WHERE mic.insurance.id = :insuranceId AND mic.brand.id = :brandId")
    Optional<MedicineInsuranceCoverage> findByInsuranceIdAndBrandId(
            @Param("insuranceId") UUID insuranceId,
            @Param("brandId") UUID brandId
    );

    @Query("SELECT mic FROM MedicineInsuranceCoverage mic WHERE mic.insurance.id = :insuranceId AND mic.variant.id = :variantId")
    Optional<MedicineInsuranceCoverage> findByInsuranceIdAndVariantId(
            @Param("insuranceId") UUID insuranceId,
            @Param("variantId") UUID variantId
    );

    // Delete methods with corrected query references
    @Modifying
    void deleteByGenericId(UUID genericId);

    @Modifying
    void deleteByBrandId(UUID brandId);

    @Modifying
    void deleteByVariantId(UUID variantId);

    @Modifying
    void deleteByInsuranceId(UUID insuranceId);

    @Modifying
    @Query("DELETE FROM MedicineInsuranceCoverage mic WHERE mic.insurance.id = :insuranceId AND mic.generic.id = :genericId")
    void deleteByInsuranceIdAndGenericId(@Param("insuranceId") UUID insuranceId, @Param("genericId") UUID genericId);

    @Modifying
    @Query("DELETE FROM MedicineInsuranceCoverage mic WHERE mic.insurance.id = :insuranceId AND mic.brand.id = :brandId")
    void deleteByInsuranceIdAndBrandId(@Param("insuranceId") UUID insuranceId, @Param("brandId") UUID brandId);

    @Modifying
    @Query("DELETE FROM MedicineInsuranceCoverage mic WHERE mic.insurance.id = :insuranceId AND mic.variant.id = :variantId")
    void deleteByInsuranceIdAndVariantId(@Param("insuranceId") UUID insuranceId, @Param("variantId") UUID variantId);

    // Added methods for sync functionality
    @Query("SELECT mic FROM MedicineInsuranceCoverage mic WHERE mic.updatedAt > :timestamp")
    List<MedicineInsuranceCoverage> findUpdatedAfter(@Param("timestamp") Instant timestamp);

    @Query("SELECT mic FROM MedicineInsuranceCoverage mic WHERE mic.createdAt > :timestamp")
    List<MedicineInsuranceCoverage> findCreatedAfter(@Param("timestamp") Instant timestamp);

    @Query("SELECT mic FROM MedicineInsuranceCoverage mic WHERE mic.syncVersion > :lastSyncVersion ORDER BY mic.syncVersion ASC")
    Page<MedicineInsuranceCoverage> findBySyncVersionGreaterThan(@Param("lastSyncVersion") Double lastSyncVersion, Pageable pageable);
}