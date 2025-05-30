package com.nexxserve.authservice.config;

    import com.nexxserve.authservice.security.JwtAuthenticationFilter;
    import com.nexxserve.authservice.security.SecurityExpressionMethods;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.security.authentication.AuthenticationManager;
    import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
    import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
    import org.springframework.security.config.annotation.web.builders.HttpSecurity;
    import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
    import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
    import org.springframework.security.config.http.SessionCreationPolicy;
    import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
    import org.springframework.security.crypto.password.PasswordEncoder;
    import org.springframework.security.web.SecurityFilterChain;
    import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
    import org.springframework.web.reactive.function.client.WebClient;

    @Configuration
    @EnableWebSecurity
    @EnableMethodSecurity(securedEnabled = true)
    public class SecurityConfig {

        private final JwtAuthenticationFilter jwtAuthFilter;
        private final SecurityExpressionMethods securityExpressionMethods;

        @Autowired
        public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter,
                              SecurityExpressionMethods securityExpressionMethods) {
            this.jwtAuthFilter = jwtAuthFilter;
            this.securityExpressionMethods = securityExpressionMethods;
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            return http
                    .csrf(AbstractHttpConfigurer::disable)
                    .authorizeHttpRequests(auth -> auth
                            .requestMatchers("/auth/login", "/auth/users/**", "/auth/register", "/auth/**", "/auth/public/**", "/api/permissions/**", "/api/permissions").permitAll()
                            .anyRequest().authenticated()
                    )
                    .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                    .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                    .build();
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }

        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
            return config.getAuthenticationManager();
        }

        @Bean
        public WebClient webClient() {
            return WebClient.builder().build();
        }

        @Bean
        public SecurityExpressionMethods securityExpressionMethods() {
            return securityExpressionMethods;
        }
    }