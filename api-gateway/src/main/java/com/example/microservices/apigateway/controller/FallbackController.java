package com.example.microservices.apigateway.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    private static final Logger logger = LoggerFactory.getLogger(FallbackController.class);

    private Mono<ResponseEntity<String>> createFallbackResponse(String serviceName) {
        String message = String.format("%s is currently unavailable. Please try again later.", serviceName);
        logger.warn("Fallback triggered for {}", serviceName);
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(message));
    }

    @GetMapping("/product-service")
    public Mono<ResponseEntity<String>> productServiceFallback() {
        return createFallbackResponse("Product Service");
    }

    @GetMapping("/inventory-service")
    public Mono<ResponseEntity<String>> inventoryServiceFallback() {
        return createFallbackResponse("Inventory Service");
    }

    @GetMapping("/order-service")
    public Mono<ResponseEntity<String>> orderServiceFallback() {
        return createFallbackResponse("Order Service");
    }

    @GetMapping("/payment-service")
    public Mono<ResponseEntity<String>> paymentServiceFallback() {
        return createFallbackResponse("Payment Service");
    }
}
