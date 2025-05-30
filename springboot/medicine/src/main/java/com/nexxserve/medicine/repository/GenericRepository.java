package com.nexxserve.medicine.repository;

import com.nexxserve.medicine.entity.Generic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface GenericRepository extends JpaRepository<Generic, UUID> {

    @Query("SELECT g FROM Generic g LEFT JOIN FETCH g.therapeuticClass WHERE LOWER(g.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Generic> findByNameContainingIgnoreCase(@Param("name") String name);

    @Query("SELECT g FROM Generic g LEFT JOIN FETCH g.therapeuticClass WHERE g.therapeuticClass.id = :classId")
    List<Generic> findByTherapeuticClassId(@Param("classId") UUID classId);

    List<Generic> findByIsParent(Boolean isParent);
}