package com.nexxserve.inventoryservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@ConfigurationProperties(prefix = "sync")
@Data
public class SyncConfiguration {

    private Session session = new Session();
    private Performance performance = new Performance();
    private Monitoring monitoring = new Monitoring();

    @Data
    public static class Session {
        private long expiryTimeHours = 24;
        private long cleanupIntervalHours = 1;
        private int maxConcurrentSessionsPerDevice = 1;
        private long stuckSessionThresholdHours = 2;
        private long oldSessionRetentionDays = 7;
    }

    @Data
    public static class Performance {
        private int defaultPageSize = 500;
        private int maxPageSize = 1000;
        private int minPageSize = 50;
        private long networkTimeoutSeconds = 30;
        private int maxRetryAttempts = 3;
    }

    @Data
    public static class Monitoring {
        private long statisticsIntervalMinutes = 30;
        private long integrityCheckIntervalHours = 6;
        private int maxActiveSessionsWarningThreshold = 50;
        private boolean enableDetailedLogging = false;
    }
}

// Alternative configuration using @Value annotations
/*
@Configuration
@EnableScheduling
public class SyncConfiguration {

    @Value("${sync.session.expiry-time-hours:24}")
    private long sessionExpiryTimeHours;

    @Value("${sync.session.cleanup-interval-hours:1}")
    private long cleanupIntervalHours;

    @Value("${sync.session.max-concurrent-per-device:1}")
    private int maxConcurrentSessionsPerDevice;

    @Value("${sync.performance.default-page-size:500}")
    private int defaultPageSize;

    @Value("${sync.performance.max-page-size:1000}")
    private int maxPageSize;

    @Value("${sync.monitoring.statistics-interval-minutes:30}")
    private long statisticsIntervalMinutes;

    // Getters...
}
*/