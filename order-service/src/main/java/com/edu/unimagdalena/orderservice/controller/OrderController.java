package com.edu.unimagdalena.orderservice.controller;

import com.edu.unimagdalena.orderservice.entity.Order;
import com.edu.unimagdalena.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/orden")
@RequiredArgsConstructor
public class OrderController {

        private final OrderService orderService;

        @GetMapping
        public Flux<ResponseEntity<Order>> getAllOrder() {
                return orderService.getAllOrder()
                                .map(order -> ResponseEntity.status(HttpStatus.OK).body(order));
        }

        @GetMapping("/{id}")
        public Mono<ResponseEntity<Order>> getOrderById(@PathVariable UUID id) {
                return orderService.getOrderById(id)
                                .map(order -> ResponseEntity.ok(order))
                                .defaultIfEmpty(ResponseEntity.notFound().build());
        }

        @PostMapping
        public Mono<ResponseEntity<Order>> createOrder(@RequestBody Order order) {
                ServletUriComponentsBuilder location = ServletUriComponentsBuilder.fromCurrentRequest();
                return orderService.createOrder(order)
                                .map(createdOrder -> ResponseEntity
                                                .created(location.path("/{id}")
                                                                .buildAndExpand(createdOrder.getId())
                                                                .toUri())
                                                .body(createdOrder));
        }

        @PutMapping("/{id}")
        public Mono<ResponseEntity<Order>> updateOrder(@PathVariable UUID id, @RequestBody Order order) {
                return orderService.updateOrder(id, order)
                                .map(ResponseEntity::ok)
                                .defaultIfEmpty(ResponseEntity.notFound().build());
        }

        @DeleteMapping("/{id}")
        public Mono<ResponseEntity<Order>> deleteOrder(@PathVariable UUID id) {
                return orderService.deleteOrder(id)
                                .map(ResponseEntity::ok)
                                .defaultIfEmpty(ResponseEntity.notFound().build());
        }
}
