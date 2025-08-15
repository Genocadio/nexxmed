package com.nexxserve.medadmin.security;

import com.nexxserve.medadmin.enums.Role;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.JoinPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

@Aspect
@Component
public class HasRoleAspect {

    @Autowired
    private com.nexxserve.medadmin.service.security.JwtService jwtService;

    @Before("@annotation(hasRole)")
    public void checkRole(JoinPoint joinPoint, HasRole hasRole) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new SecurityException("Missing or invalid Authorization header");
        }
        String token = authHeader.substring(7);
        List<String> userRoles = jwtService.getRolesFromToken(token);
        for (String requiredRole : hasRole.value()) {
            if (userRoles.contains(requiredRole)) {
                return;
            }
        }
        throw new SecurityException("Access denied: insufficient role");
    }
}