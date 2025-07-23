package com.nexxserve.medadmin.repository.catalog;

import com.nexxserve.medadmin.entity.catalog.ProductVariant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, UUID> {

    // Renamed from findByFamilyId to match service call
    List<ProductVariant> findByFamilyId(UUID familyId);

    // Changed from Optional to List to match service usage
    List<ProductVariant> findBySku(String sku);


    @Query("SELECT pv FROM ProductVariant pv WHERE LOWER(pv.name) LIKE LOWER(CONCAT('%', :keyword, '%')) "
    )
    Page<ProductVariant> findByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT pv FROM ProductVariant pv JOIN pv.insuranceCoverages ic WHERE ic.insurance.id = :insuranceId " )
    List<ProductVariant> findByActiveInsuranceCoverage(@Param("insuranceId") UUID insuranceId);

    @Query("SELECT pv FROM ProductVariant pv WHERE pv.syncVersion > :lastSyncVersion ORDER BY pv.syncVersion ASC")
    Page<ProductVariant> findBySyncVersionGreaterThan(
            @Param("lastSyncVersion") Double lastSyncVersion, Pageable pageable);
}
