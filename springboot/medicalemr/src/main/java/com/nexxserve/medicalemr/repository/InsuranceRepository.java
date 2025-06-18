package com.nexxserve.medicalemr.repository;

import com.nexxserve.medicalemr.entity.Insurance;
import com.nexxserve.medicalemr.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InsuranceRepository extends JpaRepository<Insurance, String> {

    List<Insurance> findByPatient(Patient patient);

    Optional<Insurance> findByPatientAndIsPrimaryTrue(Patient patient);

    List<Insurance> findByProvider(String provider);

    @Query("SELECT i FROM Insurance i WHERE i.patient = :patient AND i.isPrimary = true")
    Optional<Insurance> findPrimaryInsuranceByPatient(@Param("patient") Patient patient);

    boolean existsByPatientAndIsPrimaryTrue(Patient patient);
}
