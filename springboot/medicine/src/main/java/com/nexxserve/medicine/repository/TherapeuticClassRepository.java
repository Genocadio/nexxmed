package com.nexxserve.medicine.repository;

import com.nexxserve.medicine.entity.TherapeuticClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TherapeuticClassRepository extends JpaRepository<TherapeuticClass, UUID> {

    @Query("SELECT tc FROM TherapeuticClass tc WHERE LOWER(tc.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<TherapeuticClass> findByNameContainingIgnoreCase(@Param("name") String name);

    boolean existsByName(String name);
}