package com.nexxserve.medadmin.repository.medicine;

import com.nexxserve.medadmin.entity.medicine.Brand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface BrandRepository extends JpaRepository<Brand, UUID> {

    @Query("SELECT b FROM Brand b LEFT JOIN FETCH b.variant WHERE LOWER(b.brandName) LIKE LOWER(CONCAT('%', :brandName, '%'))")
    List<Brand> findByBrandNameContainingIgnoreCase(@Param("brandName") String brandName);

    List<Brand> findByVariantId(UUID variantId);

    @Query("SELECT b FROM Brand b WHERE LOWER(b.manufacturer) LIKE LOWER(CONCAT('%', :manufacturer, '%'))")
    List<Brand> findByManufacturerContainingIgnoreCase(@Param("manufacturer") String manufacturer);

    @Query("SELECT b FROM Brand b WHERE b.updatedAt > :timestamp")
    List<Brand> findUpdatedAfter(@Param("timestamp") Instant timestamp);

    @Query("SELECT b FROM Brand b WHERE b.createdAt > :timestamp")
    List<Brand> findCreatedAfter(@Param("timestamp") Instant timestamp);

    @Query("SELECT b FROM Brand b LEFT JOIN FETCH b.variant WHERE b.syncVersion > :lastSyncVersion ORDER BY b.syncVersion ASC")
    Page<Brand> findBySyncVersionGreaterThan(
            @Param("lastSyncVersion") Double lastSyncVersion,
            Pageable pageable);
}