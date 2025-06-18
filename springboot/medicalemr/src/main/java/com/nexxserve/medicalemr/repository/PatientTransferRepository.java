package com.nexxserve.medicalemr.repository;

import com.nexxserve.medicalemr.entity.PatientTransfer;
import com.nexxserve.medicalemr.entity.Patient;
import com.nexxserve.medicalemr.enums.ServiceType;
import com.nexxserve.medicalemr.enums.TransferStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PatientTransferRepository extends JpaRepository<PatientTransfer, String> {

    List<PatientTransfer> findByPatient(Patient patient);

    List<PatientTransfer> findByStatus(TransferStatus status);

    List<PatientTransfer> findByFromService(ServiceType fromService);

    List<PatientTransfer> findByToService(ServiceType toService);

    @Query("SELECT pt FROM PatientTransfer pt WHERE pt.fromService = :fromService AND pt.toService = :toService")
    List<PatientTransfer> findByServiceTransfer(@Param("fromService") ServiceType fromService,
                                                @Param("toService") ServiceType toService);
}