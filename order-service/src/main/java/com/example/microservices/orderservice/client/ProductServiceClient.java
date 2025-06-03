package com.example.microservices.orderservice.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.microservices.orderservice.dto.ProductDTO;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;

@FeignClient(name = "product-service")
public interface ProductServiceClient {

    Logger logger = LoggerFactory.getLogger(ProductServiceClient.class);

    @GetMapping("/api/products/{id}")
    @CircuitBreaker(name = "productService", fallbackMethod = "getProductFallback")
    @Retry(name = "default")
    ResponseEntity<ProductDTO> getProductById(@PathVariable("id") String id);

    default ResponseEntity<ProductDTO> getProductFallback(String id, Throwable t) {
        logger.error("Fallback for getProductById: product ID {} not found or product-service down. Error: {}", id,
                t.getMessage());
        return ResponseEntity.status(503).body(new ProductDTO(id, "Default Product", 0.0));
    }
}
