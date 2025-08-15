package com.nexxserve.inventoryservice.entity.admin;

import com.nexxserve.inventoryservice.dto.admin.InventoryReportDTO;
import com.nexxserve.inventoryservice.enums.ReportStatus;
import com.nexxserve.inventoryservice.enums.ReportType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;


import java.time.LocalDateTime;

@Entity
@Table(name = "failed_reports")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FailedReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "report_type", nullable = false)
    private ReportType reportType; // INVENTORY, USER

    @Column(name = "report_data", columnDefinition = "TEXT", nullable = false)
    private String reportData; // JSON representation of the report

    @Column(name = "retry_count", nullable = false)
    private Integer retryCount = 0;

    @Column(name = "max_retries", nullable = false)
    private Integer maxRetries = 5;

    @Column(name = "next_retry_at", nullable = false)
    private LocalDateTime nextRetryAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "last_error")
    private String lastError;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ReportStatus status = ReportStatus.PENDING;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (nextRetryAt == null) {
            nextRetryAt = LocalDateTime.now().plusMinutes(1);
        }
    }
}