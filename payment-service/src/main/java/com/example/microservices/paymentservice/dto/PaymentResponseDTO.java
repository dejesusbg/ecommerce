package com.example.microservices.paymentservice.dto;

import java.util.UUID;
import java.util.Objects;

public class PaymentResponseDTO {
    private UUID id;
    private UUID orderId;
    private Double amount;
    private String paymentStatus;

    public PaymentResponseDTO() {
    }

    public PaymentResponseDTO(UUID id, UUID orderId, Double amount, String paymentStatus) {
        this.id = id;
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
        PaymentResponseDTO that = (PaymentResponseDTO) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(orderId, that.orderId) &&
                Objects.equals(amount, that.amount) &&
                Objects.equals(paymentStatus, that.paymentStatus);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, orderId, amount, paymentStatus);
    }

    @Override
    public String toString() {
        return "PaymentResponseDTO{" +
                "id=" + id +
                ", orderId=" + orderId +
                ", amount=" + amount +
                ", paymentStatus='" + paymentStatus + '\'' +
                '}';
    }
}
