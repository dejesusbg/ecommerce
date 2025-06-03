package com.example.microservices.paymentservice.controller;

import com.example.microservices.paymentservice.dto.PaymentRequestDTO;
import com.example.microservices.paymentservice.dto.PaymentResponseDTO;
import com.example.microservices.paymentservice.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<PaymentResponseDTO> processPayment(@RequestBody PaymentRequestDTO paymentRequestDTO) {
        logger.info("Received request to process payment for order ID: {}", paymentRequestDTO.getOrderId());
        try {
            PaymentResponseDTO paymentResponse = paymentService.processPayment(paymentRequestDTO);
            logger.info("Payment processed for order ID {}. Final status: {}",
                    paymentResponse.getOrderId(), paymentResponse.getPaymentStatus());

            // Determine HTTP status based on the final payment status.
            if ("SUCCESSFUL".equals(paymentResponse.getPaymentStatus())) {
                return new ResponseEntity<>(paymentResponse, HttpStatus.CREATED);
            } else if ("PENDING".equals(paymentResponse.getPaymentStatus())) {
                return new ResponseEntity<>(paymentResponse, HttpStatus.ACCEPTED);
            } else { 
                logger.warn("Payment processing for order ID {} resulted in status: {}",
                        paymentResponse.getOrderId(), paymentResponse.getPaymentStatus());
                return new ResponseEntity<>(paymentResponse, HttpStatus.BAD_REQUEST);
            }
        } catch (RuntimeException e) {
            logger.error("Error processing payment for order ID {}: {}", paymentRequestDTO.getOrderId(), e.getMessage(),
                    e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); 
        }
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<PaymentResponseDTO> getPaymentByOrderId(@PathVariable UUID orderId) {
        logger.info("Received request to get payment status for order ID: {}", orderId);
        try {
            PaymentResponseDTO paymentResponse = paymentService.getPaymentByOrderId(orderId);
            logger.info("Payment status retrieved for order ID {}: {}", orderId, paymentResponse.getPaymentStatus());
            return ResponseEntity.ok(paymentResponse);
        } catch (RuntimeException e) { 
            logger.warn("Payment not found for order ID {}: {}", orderId, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}
