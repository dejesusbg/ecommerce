package com.edu.unimagdalena.orderservice.service;

import com.edu.unimagdalena.orderservice.entity.Order;
import com.edu.unimagdalena.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    @Override
    public Flux<Order> getAllOrder() {
        return Flux.defer(() -> Flux.fromIterable(orderRepository.findAll()))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Order> getOrderById(UUID id) {
        return Mono.fromCallable(() -> orderRepository.findById(id).orElse(null))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Order> createOrder(Order order) {
        return Mono.fromCallable(() -> orderRepository.save(order))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Order> updateOrder(UUID id, Order order) {
        return getOrderById(id)
                .flatMap(existing -> {
                    order.setId(existing.getId());
                    return Mono.fromCallable(() -> orderRepository.save(order))
                            .subscribeOn(Schedulers.boundedElastic());
                });
    }

    @Override
    public Mono<Order> deleteOrder(UUID id) {
        return getOrderById(id)
                .flatMap(existing -> Mono.fromCallable(() -> {
                    orderRepository.delete(existing);
                    return existing;
                }).subscribeOn(Schedulers.boundedElastic()));
    }
}
