package com.edu.unimagdalena.paymentservice;

import com.edu.unimagdalena.paymentservice.controller.PaymentController;
import com.edu.unimagdalena.paymentservice.entity.Payment;
import com.edu.unimagdalena.paymentservice.service.PaymentService;
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

@WebFluxTest(controllers = PaymentController.class)
public class PaymentControllerTests {

    @Autowired
    private WebTestClient webTestClient;

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private PaymentController paymentController;

    private Payment payment1;
    private Payment payment2;

    @BeforeEach
    void setUp() {
        payment1 = Payment.builder().id(UUID.randomUUID()).order(UUID.randomUUID().toString()).customer("user1")
                .amount(50.0)
                .status("SUCCESS")
                .build();
        payment2 = Payment.builder().id(UUID.randomUUID()).order(UUID.randomUUID().toString())
                .customer("user2")
                .amount(75.0)
                .status("PENDING")
                .build();
    }

    @Test
    void getAllPayment_shouldReturnPayments() {
        when(paymentService.getAllPayment()).thenReturn(Flux.just(payment1, payment2));

        webTestClient.get().uri("/api/v1/pago")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Payment.class).hasSize(2).contains(payment1, payment2);
    }

    @Test
    void getPaymentById_shouldReturnPayment_whenFound() {
        when(paymentService.getPaymentById(payment1.getId())).thenReturn(Mono.just(payment1));

        webTestClient.get().uri("/api/v1/pago/{id}", payment1.getId())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Payment.class).isEqualTo(payment1);
    }

    @Test
    void getPaymentById_shouldReturnNotFound_whenNotExists() {
        UUID nonExistentId = UUID.randomUUID();
        when(paymentService.getPaymentById(nonExistentId)).thenReturn(Mono.empty());

        webTestClient.get().uri("/api/v1/pago/{id}", nonExistentId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void createPayment_shouldCreateAndReturnPayment() {
        when(paymentService.createPayment(any(Payment.class))).thenReturn(Mono.just(payment1));

        webTestClient.post().uri("/api/v1/pago")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(payment1), Payment.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Payment.class).isEqualTo(payment1);
    }
}
