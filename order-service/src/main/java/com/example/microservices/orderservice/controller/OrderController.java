package com.example.microservices.orderservice.controller;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.microservices.orderservice.dto.OrderRequestDTO;
import com.example.microservices.orderservice.dto.OrderResponseDTO;
import com.example.microservices.orderservice.service.OrderService;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponseDTO> createOrder(@RequestBody OrderRequestDTO orderRequestDTO) {
        logger.info("Received request to create order: {}", orderRequestDTO);
        try {
            OrderResponseDTO createdOrder = orderService.createOrder(orderRequestDTO);
            logger.info("Order created successfully with ID: {}", createdOrder.getId());
            return new ResponseEntity<>(createdOrder, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            logger.warn("Failed to create order due to bad request: {}", e.getMessage());
            return ResponseEntity.badRequest().body(null); // Or a more specific error DTO
        } catch (RuntimeException e) {
            logger.error("Failed to create order due to an internal error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> getOrderById(@PathVariable UUID id) {
        logger.info("Received request to get order by ID: {}", id);
        try {
            OrderResponseDTO order = orderService.getOrderById(id);
            logger.info("Order found with ID: {}", id);
            return ResponseEntity.ok(order);
        } catch (RuntimeException e) { 
            logger.warn("Order not found for ID {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}
