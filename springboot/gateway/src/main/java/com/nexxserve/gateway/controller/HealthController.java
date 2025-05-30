package com.nexxserve.gateway.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/health")
@Slf4j
public class HealthController {

    @Autowired
    private DiscoveryClient discoveryClient;

    @GetMapping
    public Mono<ResponseEntity<Map<String, Object>>> getGatewayHealth() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", LocalDateTime.now());
        health.put("service", "api-gateway");

        // Get registered services
        List<String> services = discoveryClient.getServices();
        health.put("discoveredServices", services);

        // Check specific services
        Map<String, Object> servicesHealth = new HashMap<>();

        // Check Auth Service
        boolean authServiceUp = services.contains("AuthService");
        servicesHealth.put("auth-service", Map.of(
                "status", authServiceUp ? "UP" : "DOWN",
                "instances", authServiceUp ? discoveryClient.getInstances("AuthService").size() : 0
        ));

        // Check Billing Service
        boolean billingServiceUp = services.contains("billing");
        servicesHealth.put("billing-service", Map.of(
                "status", billingServiceUp ? "UP" : "DOWN",
                "instances", billingServiceUp ? discoveryClient.getInstances("billing").size() : 0
        ));

        health.put("services", servicesHealth);

        return Mono.just(ResponseEntity.ok(health));
    }

    @GetMapping("/gateway")
    public Mono<ResponseEntity<Map<String, Object>>> getGatewayHealthDetails() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", LocalDateTime.now());
        health.put("service", "api-gateway");

        // Get registered services
        List<String> services = discoveryClient.getServices();
        health.put("discoveredServices", services);

        return Mono.just(ResponseEntity.ok(health));
    }
}