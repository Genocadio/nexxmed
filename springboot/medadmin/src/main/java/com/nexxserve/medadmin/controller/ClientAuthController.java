package com.nexxserve.medadmin.controller;

import com.nexxserve.medadmin.dto.request.CreateClientRequest;
import com.nexxserve.medadmin.dto.response.CreateClientResponse;
import com.nexxserve.medadmin.enums.ClientStatus;
import com.nexxserve.medadmin.service.ClientService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/client")
public class ClientAuthController {

    @Autowired
    private ClientService clientService;


    @GetMapping
    public ResponseEntity<List<CreateClientResponse>> getAllClients() {
        List<CreateClientResponse> clients = clientService.getAllClients();
        return ResponseEntity.ok(clients);
    }

    @PostMapping("/check-activation")
    public ResponseEntity<Map<String, Object>> checkActivation(@RequestBody Map<String, String> request) {
        String clientId = request.get("clientId");
        String password = request.get("password");

        if (clientService.authenticateClient(clientId, password).isPresent()) {
            ClientStatus status = clientService.getClientStatus(clientId);
            Map<String, Object> response = new HashMap<>();
            response.put("clientId", clientId);
            response.put("status", status);
            response.put("activated", status == ClientStatus.ACTIVE);

            if (status == ClientStatus.ACTIVE) {
                String token = clientService.generateTokenForClient(clientId, password);
                response.put("token", token);
                response.put("message", "Client is activated. Token generated.");
            } else {
                response.put("message", "Client is not yet activated. Please wait for admin approval.");
            }

            return ResponseEntity.ok(response);
        }

        return ResponseEntity.badRequest()
                .body(Map.of("error", "Invalid client credentials"));
    }

    @PostMapping
    public ResponseEntity<CreateClientResponse> createClient(@RequestBody @Valid CreateClientRequest request) {
        CreateClientResponse response = clientService.createClient(request);
        return ResponseEntity.ok(response);

    }

    @PostMapping("/refresh-token")
    public ResponseEntity<Map<String, Object>> refreshToken(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String newToken = clientService.refreshToken(token);

        Map<String, Object> response = new HashMap<>();
        if (newToken != null) {
            response.put("token", newToken);
            response.put("message", "Token refreshed successfully");
            return ResponseEntity.ok(response);
        } else {
            response.put("error", "Unable to refresh token. Token may be expired or invalid.");
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/get-token")
    public ResponseEntity<Map<String, Object>> getToken(@RequestBody Map<String, String> request) {
        String clientId = request.get("clientId");
        String password = request.get("password");

        String token = clientService.generateTokenForClient(clientId, password);
        Map<String, Object> response = new HashMap<>();

        if (token != null) {
            response.put("token", token);
            response.put("message", "Token generated successfully");
            return ResponseEntity.ok(response);
        } else {
            response.put("error", "Unable to generate token. Client may not be activated or credentials are invalid.");
            return ResponseEntity.badRequest().body(response);
        }
    }
}