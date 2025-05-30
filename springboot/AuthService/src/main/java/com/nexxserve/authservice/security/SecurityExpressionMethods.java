package com.nexxserve.authservice.security;

    import com.nexxserve.authservice.service.JwtService;
    import jakarta.servlet.http.HttpServletRequest;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.beans.factory.annotation.Value;
    import org.springframework.security.core.Authentication;
    import org.springframework.security.core.context.SecurityContextHolder;
    import org.springframework.stereotype.Component;
    import org.springframework.web.context.request.RequestContextHolder;
    import org.springframework.web.context.request.ServletRequestAttributes;

    import io.jsonwebtoken.Claims;

    import java.util.Collection;
    import java.util.Map;
    import java.util.Set;

    @Component("security")
    public class SecurityExpressionMethods {

        @Value("${spring.application.name}")
        private String serviceName;

        private final JwtService jwtService;

        @Autowired
        public SecurityExpressionMethods(JwtService jwtService) {
            this.jwtService = jwtService;
        }

        public boolean hasRole(String role) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return false;
            }

            // Extract JWT token from the request
            String token = extractToken();
            if (token == null) {
                return false;
            }

            try {
                Claims claims = jwtService.extractAllClaims(token);

                // Check if serviceAuthorizations exists in the token
                if (claims.get("serviceAuthorizations") instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Map<String, Object>> serviceAuths =
                        (Map<String, Map<String, Object>>) claims.get("serviceAuthorizations");

                    // Check for admin role in any service (global admin)
                    for (Map<String, Object> serviceAuth : serviceAuths.values()) {
                        if (serviceAuth.get("roles") instanceof Collection &&
                                ((Collection<?>) serviceAuth.get("roles")).contains("ADMIN")) {
                            return true;
                        }
                    }

                    // Check for the specific role in the current service
                    if (serviceAuths.containsKey(serviceName)) {
                        Map<String, Object> currentServiceAuth = serviceAuths.get(serviceName);
                        if (currentServiceAuth.get("roles") instanceof Collection) {
                            @SuppressWarnings("unchecked")
                            Collection<String> roles = (Collection<String>) currentServiceAuth.get("roles");
                            return roles.contains(role);
                        }
                    }
                }
            } catch (Exception e) {
                return false;
            }

            return false;
        }

        public boolean hasPermission(String resource, String action) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return false;
            }

            // Extract JWT token from the request
            String token = extractToken();
            if (token == null) {
                return false;
            }

            try {
                Claims claims = jwtService.extractAllClaims(token);

                // Format the permission string
                String permissionString = resource + "." + action;

                // Check if serviceAuthorizations exists in the token
                if (claims.get("serviceAuthorizations") instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Map<String, Object>> serviceAuths =
                        (Map<String, Map<String, Object>>) claims.get("serviceAuthorizations");

                    // Check for admin role in any service (global admin)
                    for (Map<String, Object> serviceAuth : serviceAuths.values()) {
                        if (serviceAuth.get("roles") instanceof Collection &&
                                ((Collection<?>) serviceAuth.get("roles")).contains("ADMIN")) {
                            return true;
                        }
                    }

                    // Check for the specific permission in the current service
                    if (serviceAuths.containsKey(serviceName)) {
                        Map<String, Object> currentServiceAuth = serviceAuths.get(serviceName);
                        if (currentServiceAuth.get("permissions") instanceof Collection) {
                            @SuppressWarnings("unchecked")
                            Collection<String> permissions = (Collection<String>) currentServiceAuth.get("permissions");
                            String servicePermission = serviceName + "." + permissionString;
                            return permissions.contains(servicePermission);
                        }
                    }
                }
            } catch (Exception e) {
                return false;
            }

            return false;
        }

        private String extractToken() {
            try {
                HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
                String authHeader = request.getHeader("Authorization");

                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    return authHeader.substring(7);
                }
            } catch (Exception e) {
                // RequestContextHolder might not be available in some contexts
            }

            return null;
        }
    }