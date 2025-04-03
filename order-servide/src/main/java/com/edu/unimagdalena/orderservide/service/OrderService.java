package com.edu.unimagdalena.orderservide.service;

import com.edu.unimagdalena.orderservide.entity.Order;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface OrderService{
    Flux<Order> getAllOrder();
    Mono<Order> getOrderById(UUID id);
    Mono<Order> createOrder(Order order);
    Mono<Order> updateOrder(UUID id, Order order);
    Mono<Order> deleteOrder(UUID id);
}
