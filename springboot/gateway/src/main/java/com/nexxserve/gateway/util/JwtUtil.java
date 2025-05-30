package com.nexxserve.gateway.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private Long expiration;

    /**
     * Get the signing key from the secret
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = Base64.getDecoder().decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Extract all claims from JWT token
     */
    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Extract username from JWT token
     */
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    /**
     * Extract user ID from JWT token
     */
    public String extractUserId(String token) {
        return extractAllClaims(token).get("user_id", String.class);
    }

    /**
     * Extract roles from JWT token
     */
    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {
        return extractAllClaims(token).get("roles", List.class);
    }

    /**
     * Extract permissions from JWT token
     */
    @SuppressWarnings("unchecked")
    public List<String> extractPermissions(String token) {
        return extractAllClaims(token).get("permissions", List.class);
    }

    /**
     * Extract expiration date from JWT token
     */
    public Date extractExpiration(String token) {
        return extractAllClaims(token).getExpiration();
    }

    /**
     * Check if JWT token is expired
     */
    public Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Validate JWT token
     */
    public Boolean validateToken(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if user has required role
     */
    public Boolean hasRole(String token, String requiredRole) {
        List<String> roles = extractRoles(token);
        return roles != null && roles.contains(requiredRole);
    }

    /**
     * Check if user has required permission
     */
    public Boolean hasPermission(String token, String requiredPermission) {
        List<String> permissions = extractPermissions(token);
        return permissions != null && permissions.contains(requiredPermission);
    }

    /**
     * Check if user has any of the required roles
     */
    public Boolean hasAnyRole(String token, List<String> requiredRoles) {
        List<String> userRoles = extractRoles(token);
        if (userRoles == null || requiredRoles == null) {
            return false;
        }
        return userRoles.stream().anyMatch(requiredRoles::contains);
    }

    /**
     * Check if user has any of the required permissions
     */
    public Boolean hasAnyPermission(String token, List<String> requiredPermissions) {
        List<String> userPermissions = extractPermissions(token);
        if (userPermissions == null || requiredPermissions == null) {
            return false;
        }
        return userPermissions.stream().anyMatch(requiredPermissions::contains);
    }
}