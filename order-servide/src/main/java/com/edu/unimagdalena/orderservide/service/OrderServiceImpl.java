package com.edu.unimagdalena.orderservide.service;

import com.edu.unimagdalena.orderservide.entity.Order;
import com.edu.unimagdalena.orderservide.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService{

    private final OrderRepository orderRepository;

    @Override
    public Flux<Order> getAllOrder() {
        return null;
    }

    @Override
    public Mono<Order> getOrderById(UUID id) {
        return null;
    }

    @Override
    public Mono<Order> createOrder(Order order) {
        return null;
    }

    @Override
    public Mono<Order> updateOrder(UUID id, Order order) {
        return null;
    }

    @Override
    public Mono<Order> deleteOrder(UUID id) {
        return null;
    }
}
