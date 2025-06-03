package com.example.microservices.paymentservice.model;

import jakarta.persistence.*;
import java.util.UUID;
import java.util.Objects;

@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false, unique = true)
    private UUID orderId;

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false)
    private String paymentStatus; // PENDING, SUCCESSFUL, FAILED

    public Payment() {
    }

    public Payment(UUID orderId, Double amount, String paymentStatus) {
        this.orderId = orderId;
        this.amount = amount;
        this.paymentStatus = paymentStatus;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Payment payment = (Payment) o;
        return Objects.equals(id, payment.id) &&
                Objects.equals(orderId, payment.orderId) &&
                Objects.equals(amount, payment.amount) &&
                Objects.equals(paymentStatus, payment.paymentStatus);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, orderId, amount, paymentStatus);
    }

    @Override
    public String toString() {
        return "Payment{" +
                "id=" + id +
                ", orderId=" + orderId +
                ", amount=" + amount +
                ", paymentStatus='" + paymentStatus + '\'' +
                '}';
    }
}
