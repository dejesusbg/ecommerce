package com.edu.unimagdalena.paymentservice.service;

import com.edu.unimagdalena.paymentservice.entity.Payment;
import com.edu.unimagdalena.paymentservice.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService{

    private final PaymentRepository paymentRepository;

    @Override
    public Flux<Payment> getAllPayment() {
        return Flux.defer(() -> Flux.fromIterable(paymentRepository.findAll()))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Payment> getPaymentById(UUID id) {
        return Mono.fromCallable(() -> paymentRepository.findById(id).orElse(null))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Payment> createPayment(Payment payment) {
        return Mono.fromCallable(() -> paymentRepository.save(payment))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Payment> updatePayment(UUID id, Payment payment) {
        return getPaymentById(id)
                .flatMap(existing -> {
                    payment.setId(existing.getId());
                    return Mono.fromCallable(() -> paymentRepository.save(payment))
                            .subscribeOn(Schedulers.boundedElastic());
                });
    }

    @Override
    public Mono<Payment> deletePayment(UUID id) {
        return getPaymentById(id)
                .flatMap(existing ->
                    Mono.fromCallable(() -> {
                        paymentRepository.delete(existing);
                        return existing;
                    }).subscribeOn(Schedulers.boundedElastic())
                );
    }
}
