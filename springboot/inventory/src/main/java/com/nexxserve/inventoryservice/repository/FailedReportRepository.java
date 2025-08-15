package com.nexxserve.inventoryservice.repository;

import com.nexxserve.inventoryservice.entity.admin.FailedReport;
import com.nexxserve.inventoryservice.enums.ReportStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FailedReportRepository extends JpaRepository<FailedReport, Long> {

    @Query("SELECT fr FROM FailedReport fr WHERE fr.status IN ('PENDING', 'RETRYING') " +
            "AND fr.nextRetryAt <= :now AND fr.retryCount < fr.maxRetries")
    List<FailedReport> findReportsReadyForRetry(@Param("now") LocalDateTime now);

    @Query("SELECT fr FROM FailedReport fr WHERE fr.status = 'SUCCESS'")
    List<FailedReport> findSuccessfulReports();

    @Query("SELECT COUNT(fr) FROM FailedReport fr WHERE fr.status IN ('PENDING', 'RETRYING')")
    long countPendingReports();

    void deleteByStatus(ReportStatus status);

    void deleteByCreatedAtBeforeAndStatus(LocalDateTime dateTime, ReportStatus status);
}