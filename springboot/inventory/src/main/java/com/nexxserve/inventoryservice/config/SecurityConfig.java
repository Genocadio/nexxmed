package com.nexxserve.inventoryservice.config;

import com.nexxserve.inventoryservice.security.ClientActivationFilter;
import com.nexxserve.inventoryservice.security.JwtAuthenticationEntryPoint;
import com.nexxserve.inventoryservice.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final ClientActivationFilter clientActivationFilter;
    private final DynamicCorsConfigurationSource dynamicCorsConfigurationSource;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(@Lazy UserDetailsService userDetailsService) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(dynamicCorsConfigurationSource))
                .exceptionHandling(ex -> ex.authenticationEntryPoint(jwtAuthenticationEntryPoint))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authz -> authz
                        // Public API endpoints
                        .requestMatchers("/api/auth/login", "/api/auth/register", "/api/auth/refresh", "/api/api/sync/***", "/api/api/session/**").permitAll()
                        .requestMatchers("/api/actuator/**").permitAll()
                        .requestMatchers("/api/activation").permitAll()
                        .requestMatchers("/api/swagger-ui/**", "/api/v3/api-docs/**").permitAll()
                        .requestMatchers("/api/command/**").permitAll()

                        // Admin only endpoints
                        .requestMatchers(HttpMethod.PUT, "/api/auth/users/*/status").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/auth/users/*/deactivate").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/auth/users/*/activate").hasRole("ADMIN")

                        // Manager and Admin endpoints
                        .requestMatchers(HttpMethod.GET, "/api/auth/users/*").hasAnyRole("ADMIN", "MANAGER")

                        // Authenticated user endpoints
                        .requestMatchers("/api/auth/me", "/api/auth/change-password").authenticated()

                        // All other API requests require authentication
                        .requestMatchers("/api/**").authenticated()

                        // Allow ALL non-API requests (frontend routes and static resources)
                        .anyRequest().permitAll()
                )
                .authenticationProvider(authenticationProvider(null)) // Will be injected
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(clientActivationFilter, JwtAuthenticationFilter.class);

        return http.build();
    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(Arrays.asList("Authorization", "X-User-Id"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}