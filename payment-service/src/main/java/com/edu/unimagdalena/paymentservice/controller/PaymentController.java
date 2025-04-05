package com.edu.unimagdalena.paymentservice.controller;

import com.edu.unimagdalena.paymentservice.entity.Payment;
import com.edu.unimagdalena.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/pago")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping
    public Flux<ResponseEntity<Payment>> getAllPayment() {
        return paymentService.getAllPayment()
                .map(payment -> ResponseEntity.status(HttpStatus.OK).body(payment));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Payment>> getPaymentById(@PathVariable UUID id) {
        return paymentService.getPaymentById(id)
                .map(payment -> ResponseEntity.ok(payment))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<ResponseEntity<Payment>> createPayment(@RequestBody Payment payment) {
        ServletUriComponentsBuilder location = ServletUriComponentsBuilder.fromCurrentRequest();
        return paymentService.createPayment(payment)
                .map(createdPayment -> ResponseEntity
                        .created(
                                location.path("/{id}")
                                .buildAndExpand(createdPayment.getId())
                                .toUri())
                        .body(createdPayment)
                );
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Payment>> updatePayment(@PathVariable UUID id, @RequestBody Payment payment) {
        return paymentService.updatePayment(id, payment)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Payment>> deletePayment(@PathVariable UUID id) {
        return paymentService.deletePayment(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
