package com.nexxserve.inventoryservice.controller;

import com.nexxserve.inventoryservice.dto.ActivateClientRequest;
import com.nexxserve.inventoryservice.dto.Activationresponse;
import com.nexxserve.inventoryservice.service.admin.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/activation")
public class ActivationController {
    private final ClientService clientService;

    @PostMapping
    public ResponseEntity<Activationresponse> activateClient(@RequestBody ActivateClientRequest request) {
       Activationresponse response =  clientService.activateClient(request);
        return ResponseEntity.ok(response);
    }


}
