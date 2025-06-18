package com.nexxserve.medicalemr.repository;

import com.nexxserve.medicalemr.entity.Visit;
import com.nexxserve.medicalemr.entity.Patient;
import com.nexxserve.medicalemr.enums.ServiceType;
import com.nexxserve.medicalemr.enums.VisitStatus;
import com.nexxserve.medicalemr.enums.PriorityLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VisitRepository extends JpaRepository<Visit, String> {

    List<Visit> findByPatient(Patient patient);

    List<Visit> findByDepartment(ServiceType department);

    List<Visit> findByStatus(VisitStatus status);

    List<Visit> findByPriority(PriorityLevel priority);

    @Query("SELECT v FROM Visit v WHERE v.department = :department AND v.status = :status")
    List<Visit> findByDepartmentAndStatus(@Param("department") ServiceType department,
                                          @Param("status") VisitStatus status);

    @Query("SELECT v FROM Visit v WHERE v.createdAt BETWEEN :startTime AND :endTime")
    List<Visit> findVisitsByTimeRange(@Param("startTime") LocalDateTime startTime,
                                      @Param("endTime") LocalDateTime endTime);

    @Query("SELECT v FROM Visit v WHERE v.patient = :patient ORDER BY v.createdAt DESC")
    List<Visit> findPatientVisitsOrderByDate(@Param("patient") Patient patient);
}