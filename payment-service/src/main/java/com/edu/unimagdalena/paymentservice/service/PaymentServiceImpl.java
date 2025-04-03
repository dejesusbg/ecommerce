package com.edu.unimagdalena.paymentservice.service;

import com.edu.unimagdalena.paymentservice.entity.Payment;
import com.edu.unimagdalena.paymentservice.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService{

    private final PaymentRepository paymentRepository;

    @Override
    public Flux<Payment> getAllPayment() {
        return null;
    }

    @Override
    public Mono<Payment> getPaymentById(UUID id) {
        return null;
    }

    @Override
    public Mono<Payment> createPayment(Payment payment) {
        return null;
    }

    @Override
    public Mono<Payment> updatePayment(UUID id, Payment payment) {
        return null;
    }

    @Override
    public Mono<Payment> deletePayment(UUID id) {
        return null;
    }
}
