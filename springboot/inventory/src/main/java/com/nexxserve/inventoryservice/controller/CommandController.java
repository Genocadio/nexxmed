package com.nexxserve.inventoryservice.controller;


import com.nexxserve.inventoryservice.dto.admin.CommandRequestDto;
import com.nexxserve.inventoryservice.service.admin.CommandClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/command")
@RequiredArgsConstructor
public class CommandController {

    private final CommandClientService commandService;

    @PostMapping
    public ResponseEntity<String> processCommand(@RequestBody CommandRequestDto commandRequest) {
        log.info("Received command request: {}", commandRequest.getCommand());

        try {
            boolean success = commandService.processCommand(commandRequest);

            if (success) {
                return ResponseEntity.ok("Command executed successfully");
            } else {
                return ResponseEntity.badRequest().body("Command execution failed");
            }

        } catch (Exception e) {
            log.error("Error processing command", e);
            return ResponseEntity.internalServerError().body("Internal server error");
        }
    }
}