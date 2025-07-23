package com.nexxserve.medadmin.dto.sync;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class SyncSessionResponse {
    private String sessionId;
    private String stage;
    private Integer page;
    private Integer totalPages;
    private Long totalElements;
    private List<?> data;
    private boolean hasMorePages;
    private boolean stageCompleted;
    private boolean completed;
    private String message;
    private String nextStage;
    private SyncProgress progress;

    @Data
    @Builder
    public static class SyncProgress {
        private int currentStage;
        private int totalStages;
        private String stageName;
        private int currentPage;
        private int totalPages;
        private long recordsProcessed;
        private double completionPercentage;
    }
}