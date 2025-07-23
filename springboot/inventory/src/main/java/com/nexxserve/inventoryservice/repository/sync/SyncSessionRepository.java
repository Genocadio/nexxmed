package com.nexxserve.inventoryservice.repository.sync;

import com.nexxserve.inventoryservice.entity.sync.SyncSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SyncSessionRepository extends JpaRepository<SyncSession, String> {

    List<SyncSession> findByDeviceIdAndStatus(String deviceId, String status);

    List<SyncSession> findByDeviceIdOrderByCreatedAtDesc(String deviceId);

    List<SyncSession> findByStatus(String status);

    List<SyncSession> findByUpdatedAtBeforeAndStatusIn(LocalDateTime cutoffTime, List<String> statuses);

    @Query("SELECT s FROM SyncSession s WHERE s.deviceId = :deviceId AND s.status = 'ACTIVE' ORDER BY s.createdAt DESC")
    Optional<SyncSession> findLatestActiveSessionByDeviceId(@Param("deviceId") String deviceId);

    @Query("SELECT COUNT(s) FROM SyncSession s WHERE s.deviceId = :deviceId AND s.status = 'ACTIVE'")
    long countActiveSessionsByDeviceId(@Param("deviceId") String deviceId);

    @Query("SELECT s FROM SyncSession s WHERE s.status IN ('ACTIVE', 'PAUSED') AND s.updatedAt < :cutoffTime")
    List<SyncSession> findExpiredSessions(@Param("cutoffTime") LocalDateTime cutoffTime);

    void deleteByStatusAndUpdatedAtBefore(String status, LocalDateTime cutoffTime);

    @Query("SELECT s FROM SyncSession s WHERE s.deviceId = :deviceId AND s.status IN ('COMPLETED', 'CANCELLED', 'EXPIRED') ORDER BY s.updatedAt DESC")
    List<SyncSession> findCompletedOrCancelledSessionsByDeviceId(@Param("deviceId") String deviceId);

    long countByStatus(String status);


}