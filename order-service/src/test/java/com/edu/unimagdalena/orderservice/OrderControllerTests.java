package com.edu.unimagdalena.orderservice;

import com.edu.unimagdalena.orderservice.controller.OrderController;
import com.edu.unimagdalena.orderservice.entity.Order;
import com.edu.unimagdalena.orderservice.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = OrderController.class)
public class OrderControllerTests {

    @Autowired
    private WebTestClient webTestClient;

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    private Order order1;
    private Order order2;

    @BeforeEach
    void setUp() {
        order1 = Order.builder().id(UUID.randomUUID()).customer("user1").products(null).status("PENDING").build();
        order2 = Order.builder().id(UUID.randomUUID()).customer("user2").products(null).status("COMPLETED").build();
    }

    @Test
    void getAllOrder_shouldReturnOrders() {
        when(orderService.getAllOrder()).thenReturn(Flux.just(order1, order2));

        webTestClient.get().uri("/api/v1/orden")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Order.class).hasSize(2).contains(order1, order2);
    }

    @Test
    void getOrderById_shouldReturnOrder_whenFound() {
        when(orderService.getOrderById(order1.getId())).thenReturn(Mono.just(order1));

        webTestClient.get().uri("/api/v1/orden/{id}", order1.getId())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Order.class).isEqualTo(order1);
    }

    @Test
    void getOrderById_shouldReturnNotFound_whenNotExists() {
        UUID nonExistentId = UUID.randomUUID();
        when(orderService.getOrderById(nonExistentId)).thenReturn(Mono.empty());

        webTestClient.get().uri("/api/v1/orden/{id}", nonExistentId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void createOrder_shouldCreateAndReturnOrder() {
        // Note: ServletUriComponentsBuilder in controller needs careful handling in
        // WebFluxTest
        // For simplicity, assuming service returns the created order and controller
        // maps it.
        // If Location header is tested, it requires more setup or a different approach.
        when(orderService.createOrder(any(Order.class))).thenReturn(Mono.just(order1));

        webTestClient.post().uri("/api/v1/orden")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(order1), Order.class)
                .exchange()
                .expectStatus().isCreated() // As per controller logic using created()
                .expectBody(Order.class).isEqualTo(order1);
    }
}
