package com.nexxserve.inventoryservice.service.sync;

import com.nexxserve.inventoryservice.entity.sync.SyncSession;
import com.nexxserve.inventoryservice.repository.sync.SyncSessionRepository;
import com.nexxserve.inventoryservice.service.sync.out.SessionBasedSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SyncSchedulerService {

    private final SyncSessionRepository syncSessionRepository;
    private final SessionBasedSyncService sessionBasedSyncService;

    /**
     * Cleanup expired sync sessions every hour
     */
    @Scheduled(fixedRate = 3600000) // 1 hour in milliseconds
    @Transactional
    public void cleanupExpiredSessions() {
        try {
            log.info("Starting scheduled cleanup of expired sync sessions");
            sessionBasedSyncService.cleanupExpiredSessions();
            log.info("Completed scheduled cleanup of expired sync sessions");
        } catch (Exception e) {
            log.error("Error during scheduled session cleanup", e);
        }
    }

    /**
     * Remove old completed/cancelled sessions every 24 hours
     */
    @Scheduled(fixedRate = 86400000) // 24 hours in milliseconds
    @Transactional
    public void removeOldSessions() {
        try {
            log.info("Starting removal of old sync sessions");

            // Remove sessions older than 7 days that are completed or cancelled
            LocalDateTime cutoffTime = LocalDateTime.now().minusDays(7);

            List<String> statusesToRemove = Arrays.asList("COMPLETED", "CANCELLED", "EXPIRED");

            List<SyncSession> oldSessions = syncSessionRepository.findByUpdatedAtBeforeAndStatusIn(
                    cutoffTime, statusesToRemove);

            if (!oldSessions.isEmpty()) {
                syncSessionRepository.deleteAll(oldSessions);
                log.info("Removed {} old sync sessions", oldSessions.size());
            } else {
                log.info("No old sync sessions to remove");
            }

        } catch (Exception e) {
            log.error("Error during old session removal", e);
        }
    }

    /**
     * Log sync session statistics every 30 minutes
     */
    @Scheduled(fixedRate = 1800000) // 30 minutes in milliseconds
    public void logSyncStatistics() {
        try {
            long activeCount = syncSessionRepository.countByStatus("ACTIVE");
            long pausedCount = syncSessionRepository.countByStatus("PAUSED");
            long completedCount = syncSessionRepository.countByStatus("COMPLETED");
            long cancelledCount = syncSessionRepository.countByStatus("CANCELLED");
            long expiredCount = syncSessionRepository.countByStatus("EXPIRED");

            log.info("Sync Session Statistics - Active: {}, Paused: {}, Completed: {}, Cancelled: {}, Expired: {}",
                    activeCount, pausedCount, completedCount, cancelledCount, expiredCount);

            // Log warning if too many active sessions
            if (activeCount > 50) {
                log.warn("High number of active sync sessions detected: {}", activeCount);
            }

        } catch (Exception e) {
            log.error("Error while logging sync statistics", e);
        }
    }

    /**
     * Check for stuck sessions and mark them as expired
     */
    @Scheduled(fixedRate = 7200000) // 2 hours in milliseconds
    @Transactional
    public void checkForStuckSessions() {
        try {
            log.debug("Checking for stuck sync sessions");

            // Find sessions that haven't been updated for more than 2 hours
            LocalDateTime stuckCutoff = LocalDateTime.now().minusHours(2);

            List<SyncSession> stuckSessions = syncSessionRepository.findByUpdatedAtBeforeAndStatusIn(
                    stuckCutoff, Arrays.asList("ACTIVE", "PAUSED"));

            if (!stuckSessions.isEmpty()) {
                log.warn("Found {} potentially stuck sync sessions", stuckSessions.size());

                for (SyncSession session : stuckSessions) {
                    session.setStatus("EXPIRED");
                    session.setErrorMessage("Session marked as expired due to inactivity");
                    session.setUpdatedAt(LocalDateTime.now());

                    log.warn("Marked session {} as expired (device: {}, last update: {})",
                            session.getSessionId(), session.getDeviceId(), session.getUpdatedAt());
                }

                syncSessionRepository.saveAll(stuckSessions);
                log.info("Marked {} stuck sessions as expired", stuckSessions.size());
            }

        } catch (Exception e) {
            log.error("Error while checking for stuck sessions", e);
        }
    }

    /**
     * Validate data integrity of sync sessions
     */
    @Scheduled(fixedRate = 21600000) // 6 hours in milliseconds
    public void validateSyncSessionIntegrity() {
        try {
            log.debug("Validating sync session data integrity");

            // Find sessions with invalid stages
            List<SyncSession> allActiveSessions = syncSessionRepository.findByStatus("ACTIVE");
            List<String> validStages = Arrays.asList(
                    "INSURANCES", "THERAPEUTIC_CLASSES", "GENERICS", "VARIANTS",
                    "BRANDS", "MEDICINE_COVERAGES", "CATEGORY_REFERENCES",
                    "PRODUCT_FAMILIES", "PRODUCT_VARIANTS", "PRODUCT_INSURANCE_COVERAGES"
            );

            int invalidCount = 0;
            for (SyncSession session : allActiveSessions) {
                if (!validStages.contains(session.getCurrentStage())) {
                    log.warn("Invalid stage '{}' found in session {}",
                            session.getCurrentStage(), session.getSessionId());
                    invalidCount++;
                }

                if (session.getCurrentPage() < 0) {
                    log.warn("Invalid page number {} found in session {}",
                            session.getCurrentPage(), session.getSessionId());
                    invalidCount++;
                }
            }

            if (invalidCount > 0) {
                log.warn("Found {} data integrity issues in sync sessions", invalidCount);
            } else {
                log.debug("All sync sessions passed integrity validation");
            }

        } catch (Exception e) {
            log.error("Error during sync session integrity validation", e);
        }
    }
}