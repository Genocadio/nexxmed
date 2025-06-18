package com.nexxserve.medicalemr.repository;

import com.nexxserve.medicalemr.entity.Patient;
import com.nexxserve.medicalemr.enums.ServiceType;
import com.nexxserve.medicalemr.enums.PriorityLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, String> {

    Optional<Patient> findByNationalId(String nationalId);

    @Query("SELECT MAX(CAST(SUBSTRING(p.patientIdentifier, LENGTH(p.patientIdentifier)-3, 4) AS int)) " +
            "FROM Patient p WHERE p.patientIdentifier LIKE CONCAT('%', :yearMonth, '%')")
    int findMaxCounterForYearMonth(@Param("yearMonth") String yearMonth);

    boolean existsByPatientIdentifier(String patientIdentifier);

    @Query("SELECT p FROM Patient p WHERE " +
            "LOWER(p.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(p.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "p.nationalId LIKE CONCAT('%', :searchTerm, '%')")
    List<Patient> searchPatients(@Param("searchTerm") String searchTerm);

    boolean existsByNationalId(String nationalId);
}