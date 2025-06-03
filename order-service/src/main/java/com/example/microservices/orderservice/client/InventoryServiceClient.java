package com.example.microservices.orderservice.client;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.microservices.orderservice.dto.InventoryDTO;
import com.example.microservices.orderservice.dto.InventoryUpdateRequestDTO;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;

@FeignClient(name = "inventory-service")
public interface InventoryServiceClient {

    Logger logger = LoggerFactory.getLogger(InventoryServiceClient.class);

    @GetMapping("/api/inventory/{productId}")
    @CircuitBreaker(name = "inventoryService", fallbackMethod = "getInventoryFallback")
    @Retry(name = "default")
    ResponseEntity<Integer> getInventoryByProductId(@PathVariable("productId") UUID productId);

    @PostMapping("/api/inventory")
    @CircuitBreaker(name = "inventoryService", fallbackMethod = "updateInventoryFallback")
    @Retry(name = "default")
    ResponseEntity<InventoryDTO> updateInventory(@RequestBody InventoryUpdateRequestDTO request);

    default ResponseEntity<Integer> getInventoryFallback(UUID productId, Throwable t) {
        logger.error(
                "Fallback for getInventoryByProductId: product ID {} inventory not found or inventory-service down. Error: {}",
                productId, t.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(-1);
    }

    default ResponseEntity<InventoryDTO> updateInventoryFallback(InventoryUpdateRequestDTO request,
            Throwable t) {
        logger.error(
                "Fallback for updateInventory: product ID {} inventory update failed or inventory-service down. Error: {}",
                request.getProductId(), t.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(null);
    }
}
