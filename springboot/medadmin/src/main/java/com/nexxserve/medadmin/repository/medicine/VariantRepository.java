package com.nexxserve.medadmin.repository.medicine;

import com.nexxserve.medadmin.entity.medicine.Variant;
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
public interface VariantRepository extends JpaRepository<Variant, UUID> {

    @Query("SELECT v FROM Variant v LEFT JOIN FETCH v.generics WHERE LOWER(v.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Variant> findByNameContainingIgnoreCase(@Param("name") String name);

    @Query("SELECT v FROM Variant v JOIN v.generics g WHERE g.id = :genericId")
    List<Variant> findByGenericId(@Param("genericId") UUID genericId);

    @Query("SELECT v FROM Variant v WHERE LOWER(v.form) LIKE LOWER(CONCAT('%', :form, '%'))")
    List<Variant> findByFormContainingIgnoreCase(@Param("form") String form);

    @Query("SELECT v FROM Variant v WHERE LOWER(v.route) LIKE LOWER(CONCAT('%', :route, '%'))")
    List<Variant> findByRouteContainingIgnoreCase(@Param("route") String route);

    @Query("SELECT v FROM Variant v WHERE LOWER(v.tradeName) LIKE LOWER(CONCAT('%', :tradeName, '%'))")
    List<Variant> findByTradeNameContainingIgnoreCase(@Param("tradeName") String tradeName);

    @Query("SELECT v FROM Variant v WHERE v.updatedAt > :timestamp")
    List<Variant> findUpdatedAfter(@Param("timestamp") Instant timestamp);
    @Query("SELECT v FROM Variant v LEFT JOIN FETCH v.generics WHERE v.id = :id")
    Optional<Variant> findByIdWithGenerics(@Param("id") UUID id);

    @Query("SELECT v FROM Variant v WHERE v.createdAt > :timestamp")
    List<Variant> findCreatedAfter(@Param("timestamp") Instant timestamp);

    @Query("SELECT v FROM Variant v LEFT JOIN FETCH v.generics WHERE v.syncVersion > :lastSyncVersion ORDER BY v.syncVersion ASC")
    Page<Variant> findBySyncVersionGreaterThan(
            @Param("lastSyncVersion") Double lastSyncVersion,
            Pageable pageable);
}