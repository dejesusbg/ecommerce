package com.example.microservices.paymentservice.service;

import com.example.microservices.paymentservice.dto.PaymentRequestDTO;
import com.example.microservices.paymentservice.dto.PaymentResponseDTO;
import com.example.microservices.paymentservice.model.Payment;
import com.example.microservices.paymentservice.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    public PaymentResponseDTO processPayment(PaymentRequestDTO paymentRequestDTO) {
        logger.info("Processing payment for order ID: {}", paymentRequestDTO.getOrderId());

        Optional<Payment> existingPaymentOpt = paymentRepository.findByOrderId(paymentRequestDTO.getOrderId());
        if (existingPaymentOpt.isPresent()) {
            Payment existingPayment = existingPaymentOpt.get();
            logger.warn("Payment already processed or pending for order ID: {}. Current status: {}",
                    existingPayment.getOrderId(), existingPayment.getPaymentStatus());
            return convertToResponseDTO(existingPayment);
        }

        Payment payment = new Payment(
                paymentRequestDTO.getOrderId(),
                paymentRequestDTO.getAmount(),
                "PENDING");
        Payment savedPayment = paymentRepository.save(payment);
        logger.info("Payment for order ID {} saved with status PENDING. Payment ID: {}",
                savedPayment.getOrderId(), savedPayment.getId());

        try {
            savedPayment.setPaymentStatus("SUCCESSFUL");
            logger.info("Payment processing simulation complete for order ID {}. Status: SUCCESSFUL",
                    savedPayment.getOrderId());
        } catch (InterruptedException e) {
            logger.error("Payment processing interrupted for order ID {}. Setting status to FAILED.",
                    savedPayment.getOrderId(), e);
            savedPayment.setPaymentStatus("FAILED");
            Thread.currentThread().interrupt(); 
        }

        Payment finalPayment = paymentRepository.save(savedPayment);
        logger.info("Final payment status for order ID {}: {}", finalPayment.getOrderId(),
                finalPayment.getPaymentStatus());
        return convertToResponseDTO(finalPayment);
    }

    public PaymentResponseDTO getPaymentByOrderId(UUID orderId) {
        logger.info("Fetching payment for order ID: {}", orderId);
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> {
                    logger.warn("Payment not found for order ID: {}", orderId);
                    return new RuntimeException("Payment not found for order ID: " + orderId);
                });
        logger.info("Payment found for order ID {}. Status: {}", orderId, payment.getPaymentStatus());
        return convertToResponseDTO(payment);
    }

    private PaymentResponseDTO convertToResponseDTO(Payment payment) {
        return new PaymentResponseDTO(
                payment.getId(),
                payment.getOrderId(),
                payment.getAmount(),
                payment.getPaymentStatus());
    }
}
