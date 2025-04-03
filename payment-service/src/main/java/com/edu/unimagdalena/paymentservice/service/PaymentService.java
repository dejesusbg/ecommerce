package com.edu.unimagdalena.paymentservice.service;

import com.edu.unimagdalena.paymentservice.entity.Payment;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface PaymentService {
    Flux<Payment> getAllPayment();
    Mono<Payment> getPaymentById(UUID id);
    Mono<Payment> createPayment(Payment payment);
    Mono<Payment> updatePayment(UUID id, Payment payment);
    Mono<Payment> deletePayment(UUID id);
}
