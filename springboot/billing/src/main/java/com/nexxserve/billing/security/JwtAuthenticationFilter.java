package com.nexxserve.billing.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private final JwtConfig jwtConfig;

    public JwtAuthenticationFilter(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                   FilterChain filterChain) throws ServletException, IOException {
        logger.info("Starting JWT authentication for request to {}", request.getRequestURI());
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null) {
            logger.info("No Authorization header found in the request");
        } else if (!authHeader.startsWith("Bearer ")) {
            logger.info("Authorization header found but doesn't start with 'Bearer '");
        } else {
            String token = authHeader.substring(7);
            logger.info("JWT token extracted from Authorization header");

            try {
                logger.info("Attempting to parse JWT token");
                Claims claims = jwtConfig.parseToken(token);
                String userId = claims.getSubject();
                logger.info("JWT token successfully parsed for user: {}", userId);

                logger.info("Extracting authorities from JWT token");
                List<GrantedAuthority> authorities = jwtConfig.getAuthoritiesFromToken(claims);
                logger.info("Extracted {} authorities from token", authorities.size());

                // Create authentication object and set it in Security Context
                UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userId, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                logger.info("Authentication object created and set in SecurityContext");

            } catch (Exception e) {
                logger.error("Error processing JWT token: {}", e.getMessage(), e);
                SecurityContextHolder.clearContext();
                logger.info("SecurityContext cleared due to JWT processing error");
            }
        }

        logger.info("Proceeding with filter chain");
        filterChain.doFilter(request, response);
    }
}