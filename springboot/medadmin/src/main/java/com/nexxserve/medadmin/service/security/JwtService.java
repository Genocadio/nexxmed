package com.nexxserve.medadmin.service.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class JwtService {

    @Value("${jwt.secret:mySecretKey}")
    private String jwtSecret;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }


    public String generateAdminToken(String adminId, List<String> roles) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("adminId", adminId);
        claims.put("roles", roles);
        claims.put("type", "admin-auth");

        long expirationMillis = 24L * 60 * 60 * 1000; // 1 day
        return Jwts.builder()
                .claims(claims)
                .subject(adminId.toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMillis))
                .signWith(getSigningKey())
                .compact();
    }
    public List<String> getRolesFromToken(String token) {
        Claims claims = getClaims(token);
        return claims.get("roles", List.class);
    }

    public Long getCurrentAdminId() {
        org.springframework.security.core.Authentication authentication =
                org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.UserDetails userDetails) {
            // Assuming username is adminId, otherwise adjust accordingly
            return Long.valueOf(userDetails.getUsername());
        }
        throw new SecurityException("No authenticated admin found");
    }



    public String generateToken(String clientId, String clientName, int daysValid) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("clientId", clientId);
        claims.put("clientName", clientName);
        claims.put("type", "backend-auth");

        long expirationMillis = daysValid * 24L * 60 * 60 * 1000;
        return Jwts.builder()
                .claims(claims)
                .subject(clientId)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMillis))
                .signWith(getSigningKey())
                .compact();
    }
    public String generateRefreshToken(String clientId, String clientName, int daysValid) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("clientId", clientId);
        claims.put("clientName", clientName);
        claims.put("type", "refresh");

        long expirationMillis = daysValid * 24L * 60 * 60 * 1000;
        return Jwts.builder()
                .claims(claims)
                .subject(clientId)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMillis))
                .signWith(getSigningKey())
                .compact();
    }

        public String getClientIdFromToken(String token) {
        return getClaims(token).getSubject();
    }

    public boolean isTokenValid(String token) {
        try {
            getClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String getUserIdFromToken(String token) {
        return getClaims(token).get("clientId", String.class);
    }

    public boolean isTokenExpired(String token) {
        return getClaims(token).getExpiration().before(new Date());
    }

   private Claims getClaims(String token) {
       return Jwts.parser()
               .setSigningKey(getSigningKey())
               .build()
               .parseClaimsJws(token)
               .getBody();
   }
}