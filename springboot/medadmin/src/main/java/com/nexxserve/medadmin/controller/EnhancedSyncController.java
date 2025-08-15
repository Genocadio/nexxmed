package com.nexxserve.medadmin.controller;

import com.nexxserve.medadmin.dto.sync.SyncSessionResponse;
import com.nexxserve.medadmin.entity.sync.SyncSession;
import com.nexxserve.medadmin.service.sync.out.SessionBasedSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/session")
@RequiredArgsConstructor
@Slf4j
public class EnhancedSyncController {

    private final SessionBasedSyncService sessionBasedSyncService;

    /**
     * Start a new sync session for a device
     */
    @PostMapping("/start")
    public ResponseEntity<?> startSyncSession(
            @RequestParam String deviceId,
            @RequestParam(required = false) Double lastSyncVersion) {

        try {
            // Check if device has active sessions
            List<SyncSession> activeSessions = sessionBasedSyncService.getActiveSessions(deviceId);
            if (!activeSessions.isEmpty()) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Device " + deviceId + " already has an active sync session: " +
                                activeSessions.get(0).getSessionId());
            }

            SyncSession session = sessionBasedSyncService.createSyncSession(deviceId, lastSyncVersion);
            log.info("Started sync session {} for device {}", session.getSessionId(), deviceId);

            return ResponseEntity.ok(session);
        } catch (Exception e) {
            log.error("Failed to start sync session for device {}", deviceId, e);
            return ResponseEntity.internalServerError()
                    .body("Failed to start sync session: " + e.getMessage());
        }
    }

    /**
     * Resume an existing sync session
     */
    @PostMapping("/resume/{sessionId}")
    public ResponseEntity<?> resumeSyncSession(@PathVariable String sessionId) {
        try {
            SyncSession session = sessionBasedSyncService.resumeSyncSession(sessionId);
            log.info("Resumed sync session {}", sessionId);
            return ResponseEntity.ok(session);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("Failed to resume sync session {}", sessionId, e);
            return ResponseEntity.internalServerError()
                    .body("Failed to resume sync session: " + e.getMessage());
        }
    }

    /**
     * Get next batch of sync data for a session
     */
    @GetMapping("/next/{sessionId}")
    public ResponseEntity<?> getNextSyncData(
            @PathVariable String sessionId,
            @RequestParam(defaultValue = "500") int pageSize) {

        try {
            SyncSessionResponse response = sessionBasedSyncService.getNextSyncData(sessionId, pageSize);
            log.debug("Retrieved sync data for session {}: stage={}, page={}, records={}",
                    sessionId, response.getStage(), response.getPage(),
                    response.getData() != null ? response.getData().size() : 0);

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("Failed to get sync data for session {}", sessionId, e);
            return ResponseEntity.internalServerError()
                    .body("Failed to get sync data: " + e.getMessage());
        }
    }

    /**
     * Cancel a sync session
     */
    @PostMapping("/cancel/{sessionId}")
    public ResponseEntity<?> cancelSyncSession(@PathVariable String sessionId) {
        try {
            sessionBasedSyncService.cancelSyncSession(sessionId);
            log.info("Cancelled sync session {}", sessionId);
            return ResponseEntity.ok("Sync session cancelled successfully");
        } catch (Exception e) {
            log.error("Failed to cancel sync session {}", sessionId, e);
            return ResponseEntity.internalServerError()
                    .body("Failed to cancel sync session: " + e.getMessage());
        }
    }

    /**
     * Get sync session status
     */
    @GetMapping("/status/{sessionId}")
    public ResponseEntity<?> getSyncSessionStatus(@PathVariable String sessionId) {
        try {
            Optional<SyncSession> session = sessionBasedSyncService.getSyncSession(sessionId);
            if (session.isPresent()) {
                return ResponseEntity.ok(session.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Failed to get sync session status {}", sessionId, e);
            return ResponseEntity.internalServerError()
                    .body("Failed to get session status: " + e.getMessage());
        }
    }

    /**
     * Get all active sessions for a device
     */
    @GetMapping("/device/{deviceId}/active")
    public ResponseEntity<?> getActiveSessionsForDevice(@PathVariable String deviceId) {
        try {
            List<SyncSession> activeSessions = sessionBasedSyncService.getActiveSessions(deviceId);
            return ResponseEntity.ok(activeSessions);
        } catch (Exception e) {
            log.error("Failed to get active sessions for device {}", deviceId, e);
            return ResponseEntity.internalServerError()
                    .body("Failed to get active sessions: " + e.getMessage());
        }
    }

    /**
     * Admin endpoint to cleanup expired sessions
     */
    @PostMapping("/admin/cleanup")
    public ResponseEntity<?> cleanupExpiredSessions() {
        try {
            sessionBasedSyncService.cleanupExpiredSessions();
            return ResponseEntity.ok("Expired sessions cleaned up successfully");
        } catch (Exception e) {
            log.error("Failed to cleanup expired sessions", e);
            return ResponseEntity.internalServerError()
                    .body("Failed to cleanup expired sessions: " + e.getMessage());
        }
    }

    /**
     * Health check endpoint for sync service
     */
    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        return ResponseEntity.ok("Sync service is healthy");
    }
}