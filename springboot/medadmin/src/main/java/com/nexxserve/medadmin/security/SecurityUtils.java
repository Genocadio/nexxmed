// src/main/java/com/nexxserve/medadmin/security/SecurityUtils.java
package com.nexxserve.medadmin.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {
    public static String getCurrentClientId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (auth != null && auth.getPrincipal() instanceof String) ? (String) auth.getPrincipal() : null;
    }
}