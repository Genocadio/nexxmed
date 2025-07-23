package com.nexxserve.medadmin.repository.medicine;

import com.nexxserve.medadmin.entity.medicine.Generic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface GenericRepository extends JpaRepository<Generic, UUID> {

    @Query("SELECT g FROM Generic g LEFT JOIN FETCH g.therapeuticClass WHERE LOWER(g.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Generic> findByNameContainingIgnoreCase(@Param("name") String name);

    @Query("SELECT g FROM Generic g LEFT JOIN FETCH g.therapeuticClass WHERE g.therapeuticClass.id = :classId")
    List<Generic> findByTherapeuticClassId(@Param("classId") UUID classId);

    List<Generic> findByIsParent(Boolean isParent);

    @Query("SELECT g FROM Generic g WHERE g.updatedAt > :timestamp")
    List<Generic> findUpdatedAfter(@Param("timestamp") Instant timestamp);

    @Query("SELECT g FROM Generic g WHERE g.createdAt > :timestamp")
    List<Generic> findCreatedAfter(@Param("timestamp") Instant timestamp);
}