package com.example.microservices.apigateway.filter;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Component
public class CorrelationIdGlobalFilter implements GlobalFilter, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(CorrelationIdGlobalFilter.class);
    private static final String CORRELATION_ID_HEADER = "X-Correlation-ID";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String correlationId = request.getHeaders().getFirst(CORRELATION_ID_HEADER);

        if (correlationId == null) {
            correlationId = UUID.randomUUID().toString();
            logger.debug("No correlation ID found in header. Generated new one: {}", correlationId);
            request = request.mutate()
                    .header(CORRELATION_ID_HEADER, correlationId)
                    .build();
        } else {
            logger.debug("Found correlation ID in header: {}", correlationId);
        }

        // Add correlation ID to response headers
        String finalCorrelationId = correlationId;
        exchange.getResponse().beforeCommit(() -> {
            exchange.getResponse().getHeaders().add(CORRELATION_ID_HEADER, finalCorrelationId);
            return Mono.empty();
        });

        return chain.filter(exchange.mutate().request(request).build());
    }

    @Override
    public int getOrder() {
        // Set the order to a high precedence (lower value means higher precedence)
        // to ensure it runs before most other filters.
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
