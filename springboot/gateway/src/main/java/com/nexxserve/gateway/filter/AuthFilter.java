package com.nexxserve.gateway.filter;

    import lombok.extern.slf4j.Slf4j;
    import org.springframework.cloud.gateway.filter.GatewayFilter;
    import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
    import org.springframework.http.server.reactive.ServerHttpRequest;
    import org.springframework.stereotype.Component;

    @Component
    @Slf4j
    public class AuthFilter extends AbstractGatewayFilterFactory<AuthFilter.Config> {

        public AuthFilter() {
            super(Config.class);
        }

        @Override
        public GatewayFilter apply(Config config) {
            return (exchange, chain) -> {
                ServerHttpRequest request = exchange.getRequest();
                log.info("Gateway Request: {} {}", request.getMethod(), request.getPath());

                // No authentication check - pass through all requests
                return chain.filter(exchange)
                    .doOnSuccess(v -> log.debug("Request completed successfully: {}", request.getPath()))
                    .doOnError(e -> log.error("Request failed: {} - Error: {}", request.getPath(), e.getMessage()));
            };
        }

        public static class Config {
            // Empty config class - no properties needed for open routing
        }
    }