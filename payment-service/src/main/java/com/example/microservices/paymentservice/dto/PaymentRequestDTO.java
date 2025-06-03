package com.example.microservices.paymentservice.dto;

import java.util.UUID;
import java.util.Objects;

public class PaymentRequestDTO {
    private UUID orderId;
    private Double amount;

    public PaymentRequestDTO() {
    }

    public PaymentRequestDTO(UUID orderId, Double amount) {
        this.orderId = orderId;
        this.amount = amount;
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

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        PaymentRequestDTO that = (PaymentRequestDTO) o;
        return Objects.equals(orderId, that.orderId) &&
                Objects.equals(amount, that.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId, amount);
    }

    @Override
    public String toString() {
        return "PaymentRequestDTO{" +
                "orderId=" + orderId +
                ", amount=" + amount +
                '}';
    }
}
