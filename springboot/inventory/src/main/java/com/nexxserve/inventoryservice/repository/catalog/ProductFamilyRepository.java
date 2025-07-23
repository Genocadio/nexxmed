package com.nexxserve.inventoryservice.repository.catalog;


import com.nexxserve.inventoryservice.entity.catalog.ProductFamily;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductFamilyRepository extends JpaRepository<ProductFamily, UUID> {

    List<ProductFamily> findByCategoryId(UUID categoryId);


    List<ProductFamily> findByBrand(String brand);


    @Query("SELECT pf FROM ProductFamily pf WHERE LOWER(pf.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(pf.description) LIKE LOWER(CONCAT('%', :keyword, '%')) "
           )
    Page<ProductFamily> findByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT pf FROM ProductFamily pf JOIN pf.insuranceCoverages ic WHERE ic.insurance.id = :insuranceId ")
    List<ProductFamily> findByActiveInsuranceCoverage(@Param("insuranceId") UUID insuranceId);

    @Query("SELECT pf FROM ProductFamily pf WHERE pf.syncVersion > :lastSyncVersion ORDER BY pf.syncVersion ASC")
    Page<ProductFamily> findBySyncVersionGreaterThan(
            @Param("lastSyncVersion") Double lastSyncVersion, Pageable pageable);
}