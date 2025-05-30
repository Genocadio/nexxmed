package com.nexxserve.authservice.config;

import com.nexxserve.authservice.security.SecurityExpressionMethods;
import com.nexxserve.authservice.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@Configuration
@EnableMethodSecurity
public class MethodSecurityConfig {

    private final JwtService jwtService;

    @Autowired
    public MethodSecurityConfig(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Bean
    public MethodSecurityExpressionHandler methodSecurityExpressionHandler() {
        return new DefaultMethodSecurityExpressionHandler();
    }

    @Bean(name = "security")
    public SecurityExpressionMethods securityExpressionMethods() {
        return new SecurityExpressionMethods(jwtService);
    }
}