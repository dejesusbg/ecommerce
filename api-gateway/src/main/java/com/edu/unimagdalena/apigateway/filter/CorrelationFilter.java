package com.edu.unimagdalena.apigateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import java.util.UUID;

@Component
public class CorrelationFilter implements GlobalFilter, Ordered {

    private static final String CORRELATION_ID_HEADER = "X-Correlation-ID"; // Changed to X-Correlation-ID

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String correlationId = exchange.getRequest().getHeaders().getFirst(CORRELATION_ID_HEADER);
        if (correlationId == null) {
            correlationId = UUID.randomUUID().toString();
        }

        // Add to request headers for downstream
        ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                .header(CORRELATION_ID_HEADER, correlationId)
                .build();

        ServerWebExchange modifiedExchange = exchange.mutate().request(modifiedRequest).build();

        // Add to response headers
        final String finalCorrelationId = correlationId; // Effectively final for lambda
        exchange.getResponse().beforeCommit(() -> { // Use beforeCommit for response header modification
            exchange.getResponse().getHeaders().add(CORRELATION_ID_HEADER, finalCorrelationId);
            return Mono.empty();
        });

        return chain.filter(modifiedExchange);
    }

    @Override
    public int getOrder() {
        // HIGHEST_PRECEDENCE is good, but let's use a specific high value like -1 or
        // Ordered.HIGHEST_PRECEDENCE + some_value
        // to allow other very early filters if necessary. For now, HIGHEST_PRECEDENCE
        // is fine.
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
