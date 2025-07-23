package com.nexxserve.medadmin.repository;

import com.nexxserve.medadmin.entity.Insurance;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InsuranceRepository extends JpaRepository<Insurance, UUID> {
    Optional<Insurance> findByName(String name);
    Optional<Insurance> findByAbbreviation(String abbreviation);
    boolean existsByName(String name);
    boolean existsByAbbreviation(String abbreviation);
    List<Insurance> findByActive(Boolean active);

    @Query("SELECT i FROM Insurance i WHERE i.updatedAt > :timestamp")
    List<Insurance> findUpdatedAfter(Instant timestamp);

    @Query("SELECT i FROM Insurance i WHERE i.createdAt > :timestamp")
    List<Insurance> findCreatedAfter(Instant timestamp);

    List<Insurance> findBySyncVersionGreaterThan(Double lastSyncVersion);

    @Query("SELECT i FROM Insurance i WHERE i.syncVersion > :lastSyncVersion")
    Page<Insurance> findBySyncVersionGreaterThan(
            @Param("lastSyncVersion") Double lastSyncVersion,
            Pageable pageable);

    List<Insurance> findByIdIn(List<UUID> ids);
}