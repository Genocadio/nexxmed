package com.nexxserve.medadmin.controller;

import com.nexxserve.medadmin.dto.request.CreateClientRequest;
import com.nexxserve.medadmin.dto.response.CreateClientResponse;
import com.nexxserve.medadmin.entity.clients.Client;
import com.nexxserve.medadmin.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private ClientService clientService;

//    @PostMapping("/clients/register")
//    public ResponseEntity<Map<String, Object>> registerClient(@RequestBody Map<String, String> request) {
//        String name = request.get("name");
//        String phone = request.get("phone");
//        String baseUrl = request.get("baseUrl");
//
//        Client client = clientService.registerClient(name, phone, baseUrl);
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("clientId", client.getClientId());
//        response.put("password", client.getPassword());
//        response.put("name", client.getName());
//        response.put("phone", client.getPhone());
//        response.put("status", client.getStatus());
//        response.put("message", "Client registered successfully. Please wait for activation.");
//
//        return ResponseEntity.ok(response);
//    }

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