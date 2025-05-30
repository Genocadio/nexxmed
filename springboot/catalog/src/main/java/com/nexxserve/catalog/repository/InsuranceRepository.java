package com.nexxserve.catalog.repository;

import com.nexxserve.catalog.enums.InsuranceStatus;
import com.nexxserve.catalog.model.entity.Insurance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InsuranceRepository extends JpaRepository<Insurance, UUID> {

    Optional<Insurance> findByCode(String code);

    List<Insurance> findByStatus(InsuranceStatus status);

    @Query("SELECT i FROM Insurance i WHERE LOWER(i.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "OR LOWER(i.code) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Insurance> searchByTerm(@Param("searchTerm") String searchTerm);

    List<Insurance> findByStatusOrderByName(InsuranceStatus status);
}
