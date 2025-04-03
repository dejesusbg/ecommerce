package com.edu.unimagdalena.apigateway.filter;

import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class CacheFilter implements GlobalFilter, Ordered {

    private final Map<String, String> cache = new ConcurrentHashMap<>();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String requestPath = exchange.getRequest().getPath().toString();

        if (requestPath.startsWith("/products")) {
            if (cache.containsKey(requestPath)) {
                byte[] cachedResponse = cache.get(requestPath).getBytes(StandardCharsets.UTF_8);
                exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
                DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(cachedResponse);
                return exchange.getResponse().writeWith(Mono.just(buffer));
            }

            ServerHttpResponse originalResponse = exchange.getResponse();
            DataBufferFactory bufferFactory = originalResponse.bufferFactory();

            ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
                @Override
                public @NonNull Mono<Void> writeWith(@NonNull Publisher<? extends DataBuffer> body) {
                    return DataBufferUtils.join(Flux.from(body))
                            .flatMap(dataBuffer -> {
                                byte[] content = new byte[dataBuffer.readableByteCount()];
                                dataBuffer.read(content);
                                DataBufferUtils.release(dataBuffer);

                                String responseBody = new String(content, StandardCharsets.UTF_8);
                                cache.put(requestPath, responseBody);

                                DataBuffer buffer = bufferFactory.wrap(content);
                                return super.writeWith(Mono.just(buffer));
                            });
                }
            };

            return chain.filter(exchange.mutate().response(decoratedResponse).build());
        }

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
