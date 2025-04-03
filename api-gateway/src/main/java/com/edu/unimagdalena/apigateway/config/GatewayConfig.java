package com.edu.unimagdalena.apigateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("product-service", r -> r.path("/products/**")
                        .uri("lb://PRODUCT-SERVICE"))
                .route("order-service", r -> r.path("/orders/**")
                        .uri("lb://ORDER-SERVICE"))
                .route("inventory-service", r -> r.path("/inventory/**")
                        .uri("lb://INVENTORY-SERVICE"))
                .route("payment-service", r -> r.path("/payments/**")
                        .uri("lb://PAYMENT-SERVICE"))
                .build();
    }
}
