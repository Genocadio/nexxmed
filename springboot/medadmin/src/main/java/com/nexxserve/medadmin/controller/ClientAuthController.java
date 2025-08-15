package com.nexxserve.medadmin.controller;

import com.nexxserve.medadmin.dto.request.ActivateClientRequest;
import com.nexxserve.medadmin.dto.request.CommandRequestDto;
import com.nexxserve.medadmin.dto.request.CreateClientRequest;
import com.nexxserve.medadmin.dto.request.RefreshTokenRequest;
import com.nexxserve.medadmin.dto.response.ActivateClientResponse;
import com.nexxserve.medadmin.dto.response.CommandResponseDto;
import com.nexxserve.medadmin.dto.response.CreateClientResponse;
import com.nexxserve.medadmin.dto.response.RefreshTokenResponse;
import com.nexxserve.medadmin.security.SecurityUtils;
import com.nexxserve.medadmin.service.ClientService;
import com.nexxserve.medadmin.service.admin.CommandService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping("/api/client")
public class ClientAuthController {

    @Autowired
    private ClientService clientService;

    @Autowired
    private CommandService commandService;


    @GetMapping
    public ResponseEntity<List<CreateClientResponse>> getAllClients() {
        List<CreateClientResponse> clients = clientService.getAllClients();
        return ResponseEntity.ok(clients);
    }


    @PostMapping("/command")
    public ResponseEntity<CommandResponseDto> sendCommand(
            @RequestBody @Valid CommandRequestDto requestDto) {
        CommandResponseDto response = commandService.sendCommand(requestDto);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<CreateClientResponse> createClient(@RequestBody @Valid CreateClientRequest request) {
        CreateClientResponse response = clientService.createClient(request);
        return ResponseEntity.ok(response);

    }

    @PostMapping("/activate")
    public ResponseEntity<ActivateClientResponse> activateClient(@RequestBody @Valid  ActivateClientRequest request, HttpServletRequest httpServletRequest) {
        if (request.getClientId() == null || request.getClientId().isEmpty()) {
            throw new IllegalArgumentException("Client ID is required for activation.");
        }

        try {
            String remoteAddress = httpServletRequest.getRemoteAddr();
            String fullAddress = remoteAddress + ":5007" ;
            ActivateClientResponse response = clientService.activateClient(request, fullAddress);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ActivateClientResponse("", "","Activation failed: " + e.getMessage()));
        }
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<RefreshTokenResponse> refreshToken(
            @RequestHeader("RefreshToken") String refreshToken,
            @Valid @RequestBody RefreshTokenRequest request,
            HttpServletRequest httpServletRequest) {

        String clientIdFromToken = SecurityUtils.getCurrentClientId();
        if (clientIdFromToken == null || !clientIdFromToken.equals(request.getClientId())) {
            return ResponseEntity.status(403)
                    .body(new RefreshTokenResponse(null, null, "Client ID mismatch or unauthorized"));
        }
        String remoteAddress = httpServletRequest.getRemoteAddr();
        String fullAddress = remoteAddress + ":5007";

        RefreshTokenResponse response = clientService.handleRefreshToken(clientIdFromToken, fullAddress);
        if ("Subscription expired".equals(response.getMessage())) {
            return ResponseEntity.status(403).body(response);
        }
        if (response.getAccessToken() == null) {
            return ResponseEntity.status(401).body(response);
        }
        return ResponseEntity.ok(response);
    }
}