package com.nexxserve.authservice.security;

                import com.nexxserve.authservice.service.JwtService;
                import com.nexxserve.authservice.service.UserDetailsServiceImpl;
                import io.jsonwebtoken.Claims;
                import jakarta.servlet.FilterChain;
                import jakarta.servlet.ServletException;
                import jakarta.servlet.http.HttpServletRequest;
                import jakarta.servlet.http.HttpServletResponse;
                import org.slf4j.Logger;
                import org.slf4j.LoggerFactory;
                import org.springframework.beans.factory.annotation.Autowired;
                import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
                import org.springframework.security.core.context.SecurityContextHolder;
                import org.springframework.security.core.userdetails.UserDetails;
                import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
                import org.springframework.stereotype.Component;
                import org.springframework.web.filter.OncePerRequestFilter;

                import java.io.IOException;
                import java.util.List;

                @Component
                public class JwtAuthenticationFilter extends OncePerRequestFilter {

                    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
                    private final JwtService jwtService;
                    private final UserDetailsServiceImpl userDetailsService;

                    @Autowired
                    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsServiceImpl userDetailsService) {
                        this.jwtService = jwtService;
                        this.userDetailsService = userDetailsService;
                    }

                    @Override
                    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
                            throws ServletException, IOException {
                        final String authHeader = request.getHeader("Authorization");
                        final String jwt;
                        final String userId;

                        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                            filterChain.doFilter(request, response);
                            return;
                        }

                        jwt = authHeader.substring(7);
                        userId = jwtService.extractUserId(jwt);

                        if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userId);
                            if (jwtService.isTokenValid(jwt, userDetails)) {
                                // Extract and log token contents
                                Claims claims = jwtService.extractAllClaims(jwt);

                                // Log the user ID
                                logger.info("Token validated for user: {}", userId);

                                // Log all authorities/permissions
                                logger.info("User authorities: {}", userDetails.getAuthorities());

                                // Log specific claims from the token
                                if (claims.get("roles") != null) {
                                    logger.info("User roles from token: {}", claims.get("roles"));
                                }

                                if (claims.get("permissions") != null) {
                                    logger.info("User permissions from token: {}", claims.get("permissions"));
                                }

                                // Log all claims for debugging
                                logger.debug("All token claims: {}", claims);

                                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                        userDetails, null, userDetails.getAuthorities());
                                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                                SecurityContextHolder.getContext().setAuthentication(authToken);
                            }
                        }
                        filterChain.doFilter(request, response);
                    }
                }