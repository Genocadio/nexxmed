package com.nexxserve.inventoryservice.service.admin;

import com.nexxserve.inventoryservice.dto.admin.CommandRequestDto;
import com.nexxserve.inventoryservice.security.ClientCredentialsStore;
import com.nexxserve.inventoryservice.service.sync.in.SyncClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommandClientService {

    private final SyncClient syncClient;
    private final ObjectMapper objectMapper;
    private final ClientCredentialsStore clientCredentialsStore;

    public boolean processCommand(CommandRequestDto commandRequest) {
        log.info("Processing command: {}", commandRequest.getCommand());

        switch (commandRequest.getCommand().toUpperCase()) {
            case "PULL":
                return processPullCommand(commandRequest);
            case "DEACTIVATE":
                return processDeactivateCommand(commandRequest);
            default:
                log.warn("Unknown command: {}", commandRequest.getCommand());
                return false;
        }
    }

    private boolean processDeactivateCommand(CommandRequestDto commandRequest) {
        try {
            log.info("Executing DEACTIVATE command for device: {}", commandRequest.getDeviceId());

            // Clear all client credentials and tokens
            clientCredentialsStore.clearAllCredentials();

            log.info("Client deactivated successfully - all credentials and tokens cleared");
            return true;
        } catch (Exception e) {
            log.error("Failed to process DEACTIVATE command", e);
            return false;
        }
    }

    private boolean processPullCommand(CommandRequestDto commandRequest) {
        try {
            String deviceId = commandRequest.getDeviceId();

            if (deviceId == null || deviceId.trim().isEmpty()) {
                log.error("PULL command requires deviceId");
                return false;
            }

            log.info("Executing PULL command for device: {}", deviceId);

            // Parse optional payload for additional parameters like lastSyncVersion
            Double lastSyncVersion = null;
            if (commandRequest.getPayload() != null && !commandRequest.getPayload().trim().isEmpty()) {
                try {
                    PullCommandPayload pullPayload = objectMapper.readValue(commandRequest.getPayload(), PullCommandPayload.class);
                    lastSyncVersion = pullPayload.getLastSyncVersion();
                } catch (Exception e) {
                    log.warn("Failed to parse payload, using default values", e);
                }
                return syncClient.performCompleteSync(deviceId, lastSyncVersion);
            } else {
                return syncClient.performCompleteSync(deviceId);
            }

        } catch (Exception e) {
            log.error("Failed to process PULL command", e);
            return false;
        }
    }

    @lombok.Data
    public static class PullCommandPayload {
        private String deviceId;
        private Double lastSyncVersion;
    }
}