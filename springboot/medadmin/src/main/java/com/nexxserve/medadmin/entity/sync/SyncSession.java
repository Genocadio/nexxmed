package com.nexxserve.medadmin.entity.sync;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

@Entity
@Table(name = "sync_sessions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SyncSession {

    @Id
    @Column(name = "session_id")
    private String sessionId;

    @Column(name = "device_id", nullable = false)
    private String deviceId;

    @Column(name = "last_sync_version")
    private Double lastSyncVersion;

    @Column(name = "current_stage", nullable = false)
    private String currentStage;

    @Column(name = "current_page", nullable = false)
    private Integer currentPage;

    @Column(name = "status", nullable = false)
    private String status; // ACTIVE, PAUSED, COMPLETED, CANCELLED, EXPIRED

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "total_records_synced")
    private Long totalRecordsSynced;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
        if (currentPage == null) {
            currentPage = 0;
        }
        if (totalRecordsSynced == null) {
            totalRecordsSynced = 0L;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}