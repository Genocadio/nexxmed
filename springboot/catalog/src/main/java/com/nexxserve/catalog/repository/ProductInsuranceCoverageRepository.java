package com.nexxserve.catalog.repository;

import com.nexxserve.catalog.enums.CoverageStatus;
import com.nexxserve.catalog.model.entity.ProductInsuranceCoverage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductInsuranceCoverageRepository extends JpaRepository<ProductInsuranceCoverage, UUID> {

    List<ProductInsuranceCoverage> findByInsuranceId(UUID insuranceId);

    List<ProductInsuranceCoverage> findByProductFamilyId(UUID productFamilyId);

    List<ProductInsuranceCoverage> findByProductVariantId(UUID productVariantId);

    Optional<ProductInsuranceCoverage> findByInsuranceIdAndProductFamilyId(UUID insuranceId, UUID productFamilyId);

    Optional<ProductInsuranceCoverage> findByInsuranceIdAndProductVariantId(UUID insuranceId, UUID productVariantId);

    List<ProductInsuranceCoverage> findByStatus(CoverageStatus status);

    @Query("SELECT c FROM ProductInsuranceCoverage c WHERE c.status = :status " +
            "AND (c.effectiveTo IS NULL OR c.effectiveTo > :currentDate)")
    List<ProductInsuranceCoverage> findActiveByStatus(@Param("status") CoverageStatus status,
                                                      @Param("currentDate") LocalDateTime currentDate);

    @Query("SELECT c FROM ProductInsuranceCoverage c WHERE c.productFamily.id = :productFamilyId " +
            "AND c.status = 'ACTIVE' AND (c.effectiveTo IS NULL OR c.effectiveTo > :currentDate)")
    List<ProductInsuranceCoverage> findActiveByProductFamily(@Param("productFamilyId") UUID productFamilyId,
                                                             @Param("currentDate") LocalDateTime currentDate);

    @Query("SELECT c FROM ProductInsuranceCoverage c WHERE c.productVariant.id = :productVariantId " +
            "AND c.status = 'ACTIVE' AND (c.effectiveTo IS NULL OR c.effectiveTo > :currentDate)")
    List<ProductInsuranceCoverage> findActiveByProductVariant(@Param("productVariantId") UUID productVariantId,
                                                              @Param("currentDate") LocalDateTime currentDate);

    @Query("SELECT c FROM ProductInsuranceCoverage c WHERE c.insurance.id = :insuranceId " +
            "AND c.status = 'ACTIVE' AND (c.effectiveTo IS NULL OR c.effectiveTo > :currentDate)")
    List<ProductInsuranceCoverage> findActiveByInsurance(@Param("insuranceId") UUID insuranceId,
                                                         @Param("currentDate") LocalDateTime currentDate);

    @Query("SELECT COUNT(c) > 0 FROM ProductInsuranceCoverage c WHERE c.requiresPreApproval = true " +
            "AND ((c.productFamily.id = :productFamilyId AND c.productFamily IS NOT NULL) " +
            "OR (c.productVariant.id = :productVariantId AND c.productVariant IS NOT NULL))")
    boolean requiresPreApprovalForProduct(@Param("productFamilyId") UUID productFamilyId,
                                          @Param("productVariantId") UUID productVariantId);
}