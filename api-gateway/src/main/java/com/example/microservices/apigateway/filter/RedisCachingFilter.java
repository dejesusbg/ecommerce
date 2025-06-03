package com.example.microservices.apigateway.filter;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class RedisCachingFilter implements GlobalFilter, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(RedisCachingFilter.class);
    private final ReactiveRedisTemplate<String, byte[]> redisTemplate;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    // Define cacheable paths in application.yml or as a constant
    private final List<String> cacheablePaths = Arrays.asList("/api/products/*");
    private static final String CACHE_PREFIX = "api-gw-cache:";

    @Value("${spring.cloud.gateway.redis-cache.ttl-seconds:60}") 
    private long cacheTtlSeconds;

    public RedisCachingFilter(ReactiveRedisTemplate<String, byte[]> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        boolean isCacheable = request.getMethod() == HttpMethod.GET &&
                cacheablePaths.stream().anyMatch(pattern -> pathMatcher.match(pattern, path));

        if (!isCacheable) {
            return chain.filter(exchange);
        }

        String cacheKey = CACHE_PREFIX + request.getURI().toString();
        logger.debug("Request for cacheable path: {}. Cache key: {}", path, cacheKey);

        // Try to retrieve from cache
        return redisTemplate.opsForValue().get(cacheKey)
                .flatMap(cachedResponseBytes -> {
                    logger.info("Cache hit for key: {}", cacheKey);
                    ServerHttpResponse response = exchange.getResponse();
                    response.setStatusCode(HttpStatus.OK); 
                    response.getHeaders().add("X-Cache-Status", "HIT");
                    DataBuffer buffer = response.bufferFactory().wrap(cachedResponseBytes);
                    return response.writeWith(Flux.just(buffer));
                })
                .switchIfEmpty(Mono.<Void>defer(() -> {
                    logger.info("Cache miss for key: {}", cacheKey);
                    exchange.getResponse().getHeaders().add("X-Cache-Status", "MISS");

                    ServerHttpResponse originalResponse = exchange.getResponse();

                    ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
                        private final StringBuilder bodyBuilder = new StringBuilder();

                        @Override
                        public Mono<Void> writeWith(org.reactivestreams.Publisher<? extends DataBuffer> body) {
                            if (body instanceof Flux) {
                                Flux<? extends DataBuffer> fluxBody = (Flux<? extends DataBuffer>) body;
                                return super.writeWith(fluxBody.doOnNext(dataBuffer -> {
                                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                                    dataBuffer.read(bytes); // Capture the response body
                                    bodyBuilder.append(new String(bytes, StandardCharsets.UTF_8));
                                }).doOnComplete(() -> {
                                    // Cache it if status is cacheable (e.g. 200 OK)
                                    if (isCacheableStatus((HttpStatus) getStatusCode())) {
                                        byte[] responseBytes = bodyBuilder.toString().getBytes(StandardCharsets.UTF_8);
                                        logger.info("Caching response for key: {} with TTL: {} seconds. Size: {} bytes",
                                                cacheKey, cacheTtlSeconds, responseBytes.length);
                                        redisTemplate.opsForValue()
                                                .set(cacheKey, responseBytes, Duration.ofSeconds(cacheTtlSeconds))
                                                .subscribe(
                                                        success -> logger.debug(
                                                                "Successfully cached response for key: {}", cacheKey),
                                                        error -> logger.error("Error caching response for key: {}: {}",
                                                                cacheKey, error.getMessage()));
                                    } else {
                                        logger.warn("Response for key {} not cached due to non-cacheable status: {}",
                                                cacheKey, getStatusCode());
                                    }
                                }));
                            }
                            return super.writeWith(body);
                        }
                    };
                    return chain.filter(exchange.mutate().response(decoratedResponse).build());
                }));
    }

    private boolean isCacheableStatus(HttpStatus status) {
        return status != null && status.is2xxSuccessful();
    }

    @Override
    public int getOrder() {
        // Run after routing & security filters but before writing filters if possible.
        // Example: Ordered.LOWEST_PRECEDENCE - 10, may need adjustment;
        return 5;
    }
}
