package com.nexxserve.medadmin.controller;

import com.nexxserve.medadmin.dto.request.CreateClientRequest;
import com.nexxserve.medadmin.dto.request.LoginRequestDto;
import com.nexxserve.medadmin.dto.request.RegisterRequestDto;
import com.nexxserve.medadmin.dto.response.CreateClientResponse;
import com.nexxserve.medadmin.dto.response.RegisterResponseDto;
import com.nexxserve.medadmin.entity.Admins;
import com.nexxserve.medadmin.entity.clients.Client;
import com.nexxserve.medadmin.service.AdminService;
import com.nexxserve.medadmin.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private ClientService clientService;

    @Autowired
    private AdminService adminService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDto> register(@RequestBody RegisterRequestDto body) {
        RegisterResponseDto saved = adminService.register(body);
        return ResponseEntity.ok(saved);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto body) {
        Optional<RegisterResponseDto> token = adminService.login(body);
        return token
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(401).body(new RegisterResponseDto(null, null, null, null,  null, "Invalid credentials")));
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> me() {
        // Implement logic to get current admin info
        return ResponseEntity.ok(Map.of("message", "You are an admin!"));
    }


    @PostMapping("/clients")
    public ResponseEntity<CreateClientResponse> createClient(@RequestBody CreateClientRequest request) {
        CreateClientResponse response = clientService.createClient(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/clients/{clientId}/activate")
    public ResponseEntity<Client> activateClient(@PathVariable String clientId) {
        Client client = clientService.activateClient(clientId);
        if (client != null) {
            return ResponseEntity.ok(client);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/clients/{clientId}/deactivate")
    public ResponseEntity<Client> deactivateClient(@PathVariable String clientId) {
        Client client = clientService.deactivateClient(clientId);
        if (client != null) {
            return ResponseEntity.ok(client);
        }
        return ResponseEntity.notFound().build();
    }


    @GetMapping("/clients/expired")
    public ResponseEntity<List<Client>> getExpiredClients() {
        List<Client> clients = clientService.getExpiredClients();
        return ResponseEntity.ok(clients);
    }
}