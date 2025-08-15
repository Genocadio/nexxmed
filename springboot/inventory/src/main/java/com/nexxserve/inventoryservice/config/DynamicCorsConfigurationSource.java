package com.nexxserve.inventoryservice.config;

import com.nexxserve.inventoryservice.security.ClientCredentialsStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DynamicCorsConfigurationSource implements CorsConfigurationSource {

    private final ClientCredentialsStore clientCredentialsStore;

    @Value("${spring.profiles.active:}")
    private String activeProfiles;

    @Override
    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
        String serverUrl = clientCredentialsStore.getServerUrl();
        boolean isDevelopment = activeProfiles.contains("prod") || activeProfiles.contains("production");

        CorsConfiguration config = new CorsConfiguration();

        if (!isDevelopment) {
            // In development, allow localhost on any port + configured server URL
            List<String> allowedOrigins = new ArrayList<>();
            allowedOrigins.add("http://localhost:*");
            allowedOrigins.add("https://localhost:*");

            if (serverUrl != null) {
                allowedOrigins.add(serverUrl);
            }

            config.setAllowedOriginPatterns(allowedOrigins);
            log.debug("CORS configured for development with origins: {}", allowedOrigins);
        } else {
            // In production, only allow configured server URL
            if (serverUrl == null) {
                log.warn("No server URL configured, CORS will deny all origins");
                return new CorsConfiguration();
            }
            config.setAllowedOrigins(Collections.singletonList(serverUrl));
            log.debug("CORS configured for production with origin: {}", serverUrl);
        }

        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(Collections.singletonList("*"));
        config.setAllowCredentials(true);
        config.setExposedHeaders(Arrays.asList("Authorization", "X-User-Id"));

        return config;
    }
}