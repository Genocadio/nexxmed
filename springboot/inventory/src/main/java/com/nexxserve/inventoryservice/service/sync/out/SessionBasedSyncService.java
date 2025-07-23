package com.nexxserve.inventoryservice.service.sync.out;

import com.nexxserve.inventoryservice.dto.sync.*;
import com.nexxserve.inventoryservice.entity.sync.SyncSession;
import com.nexxserve.inventoryservice.repository.sync.SyncSessionRepository;
import com.nexxserve.inventoryservice.service.InsuranceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class SessionBasedSyncService {

    private final InsuranceService insuranceService;
    private final TherapeuticClassSyncService therapeuticClassSyncService;
    private final GenericSyncService genericSyncService;
    private final VariantSyncService variantSyncService;
    private final BrandSyncService brandSyncService;
    private final MedicineInsuranceCoverageSyncService medicineInsuranceCoverageSyncService;
    private final CategoryReferenceSyncService categoryReferenceSyncService;
    private final ProductFamilySyncService productFamilySyncService;
    private final ProductVariantSyncService productVariantSyncService;
    private final ProductInsuranceCoverageSyncService productInsuranceCoverageSyncService;
    private final SyncSessionRepository syncSessionRepository;

    // Define sync stages in order
    private static final List<String> SYNC_STAGES = Arrays.asList(
            "INSURANCES",
            "THERAPEUTIC_CLASSES",
            "GENERICS",
            "VARIANTS",
            "BRANDS",
            "MEDICINE_COVERAGES",
            "CATEGORY_REFERENCES",
            "PRODUCT_FAMILIES",
            "PRODUCT_VARIANTS",
            "PRODUCT_INSURANCE_COVERAGES"
    );

    @Transactional
    public SyncSession createSyncSession(String deviceId, Double lastSyncVersion) {
        log.info("Creating sync session for device: {}", deviceId);

        SyncSession session = new SyncSession();
        session.setSessionId(UUID.randomUUID().toString());
        session.setDeviceId(deviceId);
        session.setLastSyncVersion(lastSyncVersion);
        session.setCurrentStage("INSURANCES");
        session.setCurrentPage(0);
        session.setStatus("ACTIVE");
        session.setCreatedAt(LocalDateTime.now());
        session.setUpdatedAt(LocalDateTime.now());

        return syncSessionRepository.save(session);
    }

    @Transactional
    public SyncSession resumeSyncSession(String sessionId) {
        log.info("Resuming sync session: {}", sessionId);

        Optional<SyncSession> sessionOpt = syncSessionRepository.findById(sessionId);
        if (sessionOpt.isEmpty()) {
            throw new IllegalArgumentException("Sync session not found: " + sessionId);
        }

        SyncSession session = sessionOpt.get();
        if ("COMPLETED".equals(session.getStatus())) {
            throw new IllegalArgumentException("Sync session already completed: " + sessionId);
        }

        session.setStatus("ACTIVE");
        session.setUpdatedAt(LocalDateTime.now());

        return syncSessionRepository.save(session);
    }

    @Transactional
    public SyncSessionResponse getNextSyncData(String sessionId, int pageSize) {
        log.info("Getting next sync data for session: {}", sessionId);

        Optional<SyncSession> sessionOpt = syncSessionRepository.findById(sessionId);
        if (sessionOpt.isEmpty()) {
            throw new IllegalArgumentException("Sync session not found: " + sessionId);
        }

        SyncSession session = sessionOpt.get();

        if ("COMPLETED".equals(session.getStatus())) {
            return SyncSessionResponse.builder()
                    .sessionId(sessionId)
                    .stage(session.getCurrentStage())
                    .completed(true)
                    .message("Sync session already completed")
                    .build();
        }

        String currentStage = session.getCurrentStage();
        int currentPage = session.getCurrentPage();

        Pageable pageable = PageRequest.of(currentPage, pageSize);
        Page<?> data = getSyncDataForStage(currentStage, session.getLastSyncVersion(), pageable);

        boolean hasMorePages = !data.isLast();
        boolean stageCompleted = !hasMorePages;

        if (stageCompleted) {
            // Move to next stage
            String nextStage = getNextStage(currentStage);
            if (nextStage != null) {
                session.setCurrentStage(nextStage);
                session.setCurrentPage(0);
                session.setUpdatedAt(LocalDateTime.now());
                log.info("Stage {} completed. Moving to next stage: {}", currentStage, nextStage);
            } else {
                // All stages completed
                session.setStatus("COMPLETED");
                session.setCompletedAt(LocalDateTime.now());
                session.setUpdatedAt(LocalDateTime.now());
                log.info("All sync stages completed for session: {}", sessionId);
            }
        } else {
            // Move to next page of current stage
            session.setCurrentPage(currentPage + 1);
            session.setUpdatedAt(LocalDateTime.now());
        }

        syncSessionRepository.save(session);

        return SyncSessionResponse.builder()
                .sessionId(sessionId)
                .stage(currentStage)
                .page(currentPage)
                .totalPages(data.getTotalPages())
                .totalElements(data.getTotalElements())
                .data(data.getContent())
                .hasMorePages(hasMorePages)
                .stageCompleted(stageCompleted)
                .completed("COMPLETED".equals(session.getStatus()))
                .message(buildProgressMessage(currentStage, currentPage, data.getTotalPages(), session.getStatus()))
                .build();
    }

    private Page<?> getSyncDataForStage(String stage, Double lastSyncVersion, Pageable pageable) {
        log.debug("Fetching sync data for stage: {}, page: {}", stage, pageable.getPageNumber());

        switch (stage) {
            case "INSURANCES":
                return lastSyncVersion != null
                        ? insuranceService.findBySyncVersionGreaterThan(lastSyncVersion, pageable)
                        : insuranceService.findAll(pageable);

            case "THERAPEUTIC_CLASSES":
                return lastSyncVersion != null
                        ? therapeuticClassSyncService.findBySyncVersionGreaterThan(lastSyncVersion, pageable)
                        : therapeuticClassSyncService.findAll(pageable);

            case "GENERICS":
                return lastSyncVersion != null
                        ? genericSyncService.findBySyncVersionGreaterThan(lastSyncVersion, pageable)
                        : genericSyncService.findAll(pageable);

            case "VARIANTS":
                return lastSyncVersion != null
                        ? variantSyncService.findBySyncVersionGreaterThan(lastSyncVersion, pageable)
                        : variantSyncService.findAll(pageable);

            case "BRANDS":
                return lastSyncVersion != null
                        ? brandSyncService.findBySyncVersionGreaterThan(lastSyncVersion, pageable)
                        : brandSyncService.findAll(pageable);

            case "MEDICINE_COVERAGES":
                return lastSyncVersion != null
                        ? medicineInsuranceCoverageSyncService.findBySyncVersionGreaterThan(lastSyncVersion, pageable)
                        : medicineInsuranceCoverageSyncService.findAll(pageable);

            case "CATEGORY_REFERENCES":
                return lastSyncVersion != null
                        ? categoryReferenceSyncService.findBySyncVersionGreaterThan(lastSyncVersion, pageable)
                        : categoryReferenceSyncService.findAll(pageable);

            case "PRODUCT_FAMILIES":
                return lastSyncVersion != null
                        ? productFamilySyncService.findBySyncVersionGreaterThan(lastSyncVersion, pageable)
                        : productFamilySyncService.findAll(pageable);

            case "PRODUCT_VARIANTS":
                return lastSyncVersion != null
                        ? productVariantSyncService.findBySyncVersionGreaterThan(lastSyncVersion, pageable)
                        : productVariantSyncService.findAll(pageable);

            case "PRODUCT_INSURANCE_COVERAGES":
                return lastSyncVersion != null
                        ? productInsuranceCoverageSyncService.findBySyncVersionGreaterThan(lastSyncVersion, pageable)
                        : productInsuranceCoverageSyncService.findAll(pageable);

            default:
                throw new IllegalArgumentException("Unknown sync stage: " + stage);
        }
    }

    private String getNextStage(String currentStage) {
        int currentIndex = SYNC_STAGES.indexOf(currentStage);
        if (currentIndex == -1 || currentIndex >= SYNC_STAGES.size() - 1) {
            return null; // No next stage
        }
        return SYNC_STAGES.get(currentIndex + 1);
    }

    private String buildProgressMessage(String stage, int page, int totalPages, String status) {
        if ("COMPLETED".equals(status)) {
            return "Sync completed successfully";
        }

        int stageIndex = SYNC_STAGES.indexOf(stage) + 1;
        int totalStages = SYNC_STAGES.size();

        return String.format("Stage %d/%d (%s) - Page %d/%d",
                stageIndex, totalStages, stage, page + 1, totalPages);
    }

    @Transactional
    public void cancelSyncSession(String sessionId) {
        log.info("Cancelling sync session: {}", sessionId);

        Optional<SyncSession> sessionOpt = syncSessionRepository.findById(sessionId);
        if (sessionOpt.isPresent()) {
            SyncSession session = sessionOpt.get();
            session.setStatus("CANCELLED");
            session.setUpdatedAt(LocalDateTime.now());
            syncSessionRepository.save(session);
        }
    }

    public Optional<SyncSession> getSyncSession(String sessionId) {
        return syncSessionRepository.findById(sessionId);
    }

    public List<SyncSession> getActiveSessions(String deviceId) {
        return syncSessionRepository.findByDeviceIdAndStatus(deviceId, "ACTIVE");
    }

    @Transactional
    public void cleanupExpiredSessions() {
        log.info("Cleaning up expired sync sessions");
        LocalDateTime cutoffTime = LocalDateTime.now().minusHours(24); // Sessions older than 24 hours
        List<SyncSession> expiredSessions = syncSessionRepository.findByUpdatedAtBeforeAndStatusIn(
                cutoffTime, Arrays.asList("ACTIVE", "PAUSED"));

        for (SyncSession session : expiredSessions) {
            session.setStatus("EXPIRED");
            session.setUpdatedAt(LocalDateTime.now());
        }

        syncSessionRepository.saveAll(expiredSessions);
        log.info("Cleaned up {} expired sync sessions", expiredSessions.size());
    }
}