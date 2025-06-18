package com.nexxserve.medicine.repository;

import com.nexxserve.medicine.entity.Insurance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InsuranceRepository extends JpaRepository<Insurance, UUID> {
    Optional<Insurance> findByName(String name);
    List<Insurance> findByActive(Boolean active);
}