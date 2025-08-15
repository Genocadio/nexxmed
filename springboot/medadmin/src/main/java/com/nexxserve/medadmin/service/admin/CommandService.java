package com.nexxserve.medadmin.service.admin;

import com.nexxserve.medadmin.dto.request.CommandRequestDto;
import com.nexxserve.medadmin.dto.response.CommandResponseDto;
import com.nexxserve.medadmin.entity.clients.Client;
import com.nexxserve.medadmin.enums.ClientStatus;
import com.nexxserve.medadmin.repository.clients.ClientRepository;
import com.nexxserve.medadmin.service.sync.in.SyncClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommandService {
    private final ClientRepository clientRepository;
    private final RestTemplate restTemplate;
    private final TaskScheduler taskScheduler;
    private final SyncClient syncClient;

    public CommandResponseDto sendCommand(CommandRequestDto requestDto) {
        String id = requestDto.getDeviceId();
        Optional<Client> clientOpt = clientRepository.findById(UUID.fromString(id));
        if (clientOpt.isEmpty()) {
            CommandResponseDto response = new CommandResponseDto();
            response.setSuccess(false);
            response.setMessage("Client not found");
            return response;
        }

        Client client = clientOpt.get();
        String clientId = client.getClientId();
        requestDto.setDeviceId(clientId);

        String command = requestDto.getCommand().toUpperCase();

        switch (command) {
            case "PUSH":
                return handlePushCommand(requestDto, client);
            case "DEACTIVATE":
                return handleDeactivateCommand(requestDto, client);
            case "PULL":
            default:
                return handlePullCommand(requestDto, client);
        }
    }

    private CommandResponseDto handlePushCommand(CommandRequestDto requestDto, Client client) {
        log.info("PULL command received for client {}: {}", client.getClientId(), requestDto.getPayload());

        try {
            // Trigger complete sync for the device
            boolean syncSuccess = syncClient.performCompleteSync(client.getClientId());

            CommandResponseDto response = new CommandResponseDto();
            if (syncSuccess) {
                response.setSuccess(true);
                response.setMessage("PULL command processed successfully - sync completed");
                response.setClientResponse("Complete sync finished successfully");
            } else {
                response.setSuccess(false);
                response.setMessage("PULL command failed - sync could not be completed");
                response.setClientResponse("Sync operation failed");
            }
            return response;

        } catch (Exception e) {
            log.error("Error during PULL command sync for client {}: {}", client.getClientId(), e.getMessage());
            CommandResponseDto response = new CommandResponseDto();
            response.setSuccess(false);
            response.setMessage("PULL command failed due to error: " + e.getMessage());
            response.setClientResponse("Sync operation failed with error");
            return response;
        }
    }

    private CommandResponseDto handleDeactivateCommand(CommandRequestDto requestDto, Client client) {
        // First deactivate the client
        client.setStatus(ClientStatus.DEACTIVATED);
        clientRepository.save(client);
        log.info("Client {} deactivated", client.getClientId());

        // Then send the command
        CommandResponseDto sendResult = sendCommandToClient(requestDto, client);
        if (sendResult.isSuccess()) {
            sendResult.setMessage("Client deactivated and command sent successfully");
        } else {
            sendResult.setMessage("Client deactivated but command failed to send, retry scheduled");
        }
        return sendResult;
    }

    private CommandResponseDto handlePullCommand(CommandRequestDto requestDto, Client client) {
        return sendCommandToClient(requestDto, client);
    }

    private CommandResponseDto sendCommandToClient(CommandRequestDto requestDto, Client client) {
        String url = client.getBaseUrl() + "/inventory/command";
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "http://" + url;
        }

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, requestDto, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                CommandResponseDto dto = new CommandResponseDto();
                dto.setSuccess(true);
                dto.setMessage("Command sent successfully");
                dto.setClientResponse(response.getBody());
                return dto;
            } else {
                log.error("Error sending command to client {}", response);
                scheduleRetry(client, requestDto);
                CommandResponseDto dto = new CommandResponseDto();
                dto.setSuccess(false);
                dto.setMessage("Client unreachable, retry scheduled");
                return dto;
            }
        } catch (Exception e) {
            scheduleRetry(client, requestDto);
            log.error("Error sending command to client {} {}", client.getClientId(), e.getMessage());
            CommandResponseDto dto = new CommandResponseDto();
            dto.setSuccess(false);
            dto.setMessage("Client unreachable, retry scheduled");
            return dto;
        }
    }

    private void scheduleRetry(Client client, CommandRequestDto requestDto) {
        Runnable retryTask = new Runnable() {
            @Override
            public void run() {
                try {
                    String url = client.getBaseUrl() + "/command";
                    if (!url.startsWith("http://") && !url.startsWith("https://")) {
                        url = "http://" + url;
                    }
                    ResponseEntity<String> response = restTemplate.postForEntity(url, requestDto, String.class);
                    if (response.getStatusCode().is2xxSuccessful()) {
                        log.info("Retry successful for client {}", client.getClientId());
                    } else {
                        taskScheduler.schedule(this, java.util.Date.from(java.time.Instant.now().plus(Duration.ofMinutes(1))));
                    }
                } catch (Exception e) {
                    log.error("Retry failed for client {}, scheduling another retry", client.getClientId());
                    taskScheduler.schedule(this, java.util.Date.from(java.time.Instant.now().plus(Duration.ofMinutes(1))));
                }
            }
        };
        taskScheduler.schedule(retryTask, java.util.Date.from(java.time.Instant.now().plus(Duration.ofMinutes(1))));
    }
}