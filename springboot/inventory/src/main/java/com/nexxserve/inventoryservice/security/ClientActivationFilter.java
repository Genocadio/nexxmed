package com.nexxserve.inventoryservice.security;

import com.nexxserve.inventoryservice.service.admin.ClientService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class ClientActivationFilter extends OncePerRequestFilter {

    private final ClientService clientService;
    private final ObjectMapper objectMapper;

    // Define paths that should be accessible even when client is not activated
    private static final List<String> ALLOWED_API_PATHS = Arrays.asList(
            "/api/auth/login",
            "/api/auth/register",
            "/api/auth/refresh",
            "/api/actuator",
            "/api/swagger-ui",
            "/api/v3/api-docs",
            "/api/api/admin/clients",  // Admin endpoints for client management
            "/api/api/client",         // Client activation endpoints
            "/api/inventory/activation" // Activation endpoint
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        String method = request.getMethod();

        log.debug("Processing request: {} {}", method, requestURI);

        // Handle activation endpoint specially
        if (requestURI.startsWith("/api/inventory/activation")) {
            if (clientService.isClientActivated()) {
                // Block access to /activation if already activated
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "ALREADY_ACTIVATED");
                errorResponse.put("message", "Client is already activated. Activation endpoint is locked.");
                errorResponse.put("timestamp", System.currentTimeMillis());
                errorResponse.put("status", 403);
                String jsonResponse = objectMapper.writeValueAsString(errorResponse);
                response.getWriter().write(jsonResponse);
                return;
            } else {
                // Allow access to /activation if not activated
                filterChain.doFilter(request, response);
                return;
            }
        }

        // Check if the path requires client activation
        if (requiresClientActivation(requestURI)) {
            log.debug("Path {} requires client activation", requestURI);

            if (!clientService.isClientActivated()) {
                log.warn("Access denied to {} - client not activated", requestURI);
                sendClientNotActivatedResponse(response);
                return;
            }

            log.debug("Client is activated, allowing access to {}", requestURI);
        } else {
            log.debug("Path {} does not require client activation", requestURI);
        }

        filterChain.doFilter(request, response);
    }

    private boolean requiresClientActivation(String requestURI) {
        // If it's not an API request, never require activation (let frontend handle it)
        if (!requestURI.startsWith("/api/")) {
            return false;
        }

        // Check if it's an explicitly allowed API path
        for (String allowedPath : ALLOWED_API_PATHS) {
            if (requestURI.startsWith(allowedPath)) {
                return false;
            }
        }

        // All other API endpoints require activation
        return true;
    }

    private void sendClientNotActivatedResponse(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "CLIENT_NOT_ACTIVATED");
        errorResponse.put("message", "This application is not activated. Please contact your administrator.");
        errorResponse.put("timestamp", System.currentTimeMillis());
        errorResponse.put("status", 403);

        String jsonResponse = objectMapper.writeValueAsString(errorResponse);
        response.getWriter().write(jsonResponse);
    }
}