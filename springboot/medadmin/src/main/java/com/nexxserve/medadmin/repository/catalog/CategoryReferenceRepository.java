package com.nexxserve.medadmin.repository.catalog;

import com.nexxserve.medadmin.entity.catalog.CategoryReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryReferenceRepository extends JpaRepository<CategoryReference, UUID> {

    Optional<CategoryReference> findByCode(String code);

    List<CategoryReference> findByParentId(UUID parentId);

    // Method using removed fields replaced with alphabetical ordering
    List<CategoryReference> findAllByOrderByName();

    // Updated to not use displayOrder field
    @Query("SELECT c FROM CategoryReference c WHERE c.parent IS NULL ORDER BY c.name")
    List<CategoryReference> findRootCategories();

    @Query(value = "SELECT CASE WHEN COUNT(pf) > 0 THEN true ELSE false END FROM product_families pf WHERE pf.category_id = :categoryId", nativeQuery = true)
    boolean hasProductFamilyReferences(@Param("categoryId") UUID categoryId);

    // Sync-specific queries
    @Query("SELECT c FROM CategoryReference c WHERE c.updatedAt > :lastSyncTime ORDER BY c.updatedAt ASC")
    List<CategoryReference> findUpdatedAfter(@Param("lastSyncTime") Instant lastSyncTime);

    @Query("SELECT c FROM CategoryReference c WHERE c.createdAt > :lastSyncTime ORDER BY c.createdAt ASC")
    List<CategoryReference> findCreatedAfter(@Param("lastSyncTime") Instant lastSyncTime);

    @Query("SELECT c FROM CategoryReference c WHERE c.syncVersion > :lastSyncVersion ORDER BY c.syncVersion ASC")
    Page<CategoryReference> findBySyncVersionGreaterThan(
            @Param("lastSyncVersion") Double lastSyncVersion, Pageable pageable);
}