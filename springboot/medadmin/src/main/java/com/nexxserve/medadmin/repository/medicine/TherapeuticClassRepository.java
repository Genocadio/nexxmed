package com.nexxserve.medadmin.repository.medicine;

import com.nexxserve.medadmin.entity.medicine.TherapeuticClass;
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
public interface TherapeuticClassRepository extends JpaRepository<TherapeuticClass, UUID> {

    @Query("SELECT tc FROM TherapeuticClass tc WHERE LOWER(tc.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<TherapeuticClass> findByNameContainingIgnoreCase(@Param("name") String name);

    boolean existsByName(String name);

    // Sync-specific queries
    @Query("SELECT tc FROM TherapeuticClass tc WHERE tc.updatedAt > :lastSyncTime ORDER BY tc.updatedAt ASC")
    List<TherapeuticClass> findUpdatedAfter(@Param("lastSyncTime") Instant lastSyncTime);

    @Query("SELECT tc FROM TherapeuticClass tc WHERE tc.createdAt > :lastSyncTime ORDER BY tc.createdAt ASC")
    List<TherapeuticClass> findCreatedAfter(@Param("lastSyncTime") Instant lastSyncTime);

    Optional<TherapeuticClass> findByName(String name);

    @Query("SELECT tc FROM TherapeuticClass tc WHERE tc.syncVersion > :lastSyncVersion ORDER BY tc.syncVersion ASC")
    Page<TherapeuticClass> findBySyncVersionGreaterThan(
            @Param("lastSyncVersion") Double lastSyncVersion,
            Pageable pageable);
}
