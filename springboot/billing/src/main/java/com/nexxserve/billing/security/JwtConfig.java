package com.nexxserve.billing.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Configuration
public class JwtConfig {
    private static final Logger logger = LoggerFactory.getLogger(JwtConfig.class);

    @Value("${jwt.secret:defaultSecretKeyForDevelopmentDoNotUseInProduction}")
    private String jwtSecret;

    @Value("${spring.application.name}")
    private String serviceName;

    @Bean
    public SecretKey secretKey() {
        logger.debug("Creating HMAC SHA secret key");
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }


    public Claims parseToken(String token) {
        logger.debug("Building JWT parser with signing key");
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSignInKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            logger.debug("JWT token parsed successfully");
            return claims;
        } catch (Exception e) {
            logger.error("Error parsing JWT token: {} signing key: {} token: {}", e.getMessage(), jwtSecret, getSignInKey());
            throw e;
        }
    }

    public List<GrantedAuthority> getAuthoritiesFromToken(Claims claims) {
        logger.debug("Getting authorities from token for service: {}", serviceName);
        List<GrantedAuthority> authorities = new ArrayList<>();

        try {
            // Extract service authorizations from the JWT
            logger.debug("Attempting to extract 'serviceAuthorizations' from JWT claims");
            Map<String, Object> serviceAuthorizations =
                (Map<String, Object>) claims.get("serviceAuthorizations");

            if (serviceAuthorizations == null) {
                logger.debug("No 'serviceAuthorizations' found in token");
            } else if (!serviceAuthorizations.containsKey(serviceName)) {
                logger.debug("Token contains 'serviceAuthorizations' but none for service: {}", serviceName);
            } else {
                logger.debug("Found authorizations for service: {}", serviceName);
                Map<String, Object> serviceAuth = (Map<String, Object>) serviceAuthorizations.get(serviceName);

                // Extract roles
                List<String> roles = (List<String>) serviceAuth.get("roles");
                if (roles != null) {
                    logger.debug("Found {} roles in token", roles.size());
                    for (String role : roles) {
                        logger.debug("Adding role authority: ROLE_{}", role);
                        authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
                    }
                } else {
                    logger.debug("No roles found in token");
                }

                // Extract permissions
                List<String> permissions = (List<String>) serviceAuth.get("permissions");
                if (permissions != null) {
                    logger.debug("Found {} permissions in token", permissions.size());
                    for (String permission : permissions) {
                        logger.debug("Adding permission authority: {}", permission);
                        authorities.add(new SimpleGrantedAuthority(permission));
                    }
                } else {
                    logger.debug("No permissions found in token");
                }
            }
        } catch (Exception e) {
            logger.error("Error extracting authorities from token: {}", e.getMessage(), e);
        }

        logger.debug("Extracted {} total authorities from token", authorities.size());
        return authorities;
    }
}