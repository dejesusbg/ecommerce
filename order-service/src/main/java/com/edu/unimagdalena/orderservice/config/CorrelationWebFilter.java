package com.edu.unimagdalena.orderservice.config;

import org.slf4j.MDC;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class CorrelationWebFilter implements WebFilter {

    private static final String CORRELATION_ID_HEADER = "correlationId";

    @Override
    @NonNull
    public Mono<Void> filter(@NonNull ServerWebExchange exchange, @NonNull WebFilterChain chain) {
        String correlationId = exchange.getRequest().getHeaders().getFirst(CORRELATION_ID_HEADER);
        if (correlationId != null) {
            MDC.put(CORRELATION_ID_HEADER, correlationId);
        }

        // Ensure MDC is cleared after the request
        return chain.filter(exchange)
                .doFinally(signalType -> MDC.remove(CORRELATION_ID_HEADER));
    }
}
