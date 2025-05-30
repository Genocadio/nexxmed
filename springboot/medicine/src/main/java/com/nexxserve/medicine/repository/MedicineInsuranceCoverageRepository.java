package com.nexxserve.medicine.repository;

import com.nexxserve.medicine.entity.MedicineInsuranceCoverage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MedicineInsuranceCoverageRepository extends JpaRepository<MedicineInsuranceCoverage, UUID> {

    List<MedicineInsuranceCoverage> findByInsuranceId(UUID insuranceId);

    List<MedicineInsuranceCoverage> findByGenericId(UUID genericId);

    List<MedicineInsuranceCoverage> findByBrandId(UUID brandId);

    List<MedicineInsuranceCoverage> findByVariantId(UUID variantId);

    @Query("SELECT mic FROM MedicineInsuranceCoverage mic WHERE mic.insuranceId = :insuranceId AND mic.generic.id = :genericId")
    Optional<MedicineInsuranceCoverage> findByInsuranceIdAndGenericId(
        @Param("insuranceId") UUID insuranceId,
        @Param("genericId") UUID genericId
    );

    @Query("SELECT mic FROM MedicineInsuranceCoverage mic WHERE mic.insuranceId = :insuranceId AND mic.brand.id = :brandId")
    Optional<MedicineInsuranceCoverage> findByInsuranceIdAndBrandId(
        @Param("insuranceId") UUID insuranceId,
        @Param("brandId") UUID brandId
    );

    @Query("SELECT mic FROM MedicineInsuranceCoverage mic WHERE mic.insuranceId = :insuranceId AND mic.variant.id = :variantId")
    Optional<MedicineInsuranceCoverage> findByInsuranceIdAndVariantId(
        @Param("insuranceId") UUID insuranceId,
        @Param("variantId") UUID variantId
    );

    // Delete methods for entity-specific coverage removal
    @Modifying
    void deleteByGenericId(UUID genericId);

    @Modifying
    void deleteByBrandId(UUID brandId);

    @Modifying
    void deleteByVariantId(UUID variantId);

    @Modifying
    void deleteByInsuranceId(UUID insuranceId);

    @Modifying
    @Query("DELETE FROM MedicineInsuranceCoverage mic WHERE mic.insuranceId = :insuranceId AND mic.generic.id = :genericId")
    void deleteByInsuranceIdAndGenericId(@Param("insuranceId") UUID insuranceId, @Param("genericId") UUID genericId);

    @Modifying
    @Query("DELETE FROM MedicineInsuranceCoverage mic WHERE mic.insuranceId = :insuranceId AND mic.brand.id = :brandId")
    void deleteByInsuranceIdAndBrandId(@Param("insuranceId") UUID insuranceId, @Param("brandId") UUID brandId);

    @Modifying
    @Query("DELETE FROM MedicineInsuranceCoverage mic WHERE mic.insuranceId = :insuranceId AND mic.variant.id = :variantId")
    void deleteByInsuranceIdAndVariantId(@Param("insuranceId") UUID insuranceId, @Param("variantId") UUID variantId);
}