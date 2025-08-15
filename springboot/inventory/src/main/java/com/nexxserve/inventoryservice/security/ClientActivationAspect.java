package com.nexxserve.inventoryservice.security;

import com.nexxserve.inventoryservice.service.admin.ClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class ClientActivationAspect {

    private final ClientService clientService;

    @Before("@annotation(requireClientActivation)")
    public void checkClientActivation(JoinPoint joinPoint, RequireClientActivation requireClientActivation) {
        log.debug("Checking client activation for method: {}", joinPoint.getSignature().getName());

        if (clientService.isClientActivated()) {
            log.warn("Access denied to method {} - client not activated", joinPoint.getSignature().getName());
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    requireClientActivation.message()
            );
        }

        log.debug("Client is activated, allowing access to method: {}", joinPoint.getSignature().getName());
    }

    @Before("@within(requireClientActivation)")
    public void checkClientActivationForClass(JoinPoint joinPoint, RequireClientActivation requireClientActivation) {
        log.debug("Checking client activation for class method: {}", joinPoint.getSignature().getName());

        if (!clientService.isClientActivated()) {
            log.warn("Access denied to class method {} - client not activated", joinPoint.getSignature().getName());
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    requireClientActivation.message()
            );
        }

        log.debug("Client is activated, allowing access to class method: {}", joinPoint.getSignature().getName());
    }
}