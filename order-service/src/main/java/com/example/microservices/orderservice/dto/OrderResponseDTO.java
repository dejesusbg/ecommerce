package com.example.microservices.orderservice.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class OrderResponseDTO {
    private UUID id;
    private String customerId;
    private LocalDateTime orderDate;
    private String orderStatus;
    private List<OrderItemResponseDTO> items;
    private Double totalPrice; 

    public OrderResponseDTO() {
    }

    public OrderResponseDTO(UUID id, String customerId, LocalDateTime orderDate, String orderStatus,
            List<OrderItemResponseDTO> items, Double totalPrice) {
        this.id = id;
        this.customerId = customerId;
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
        this.items = items;
        this.totalPrice = totalPrice;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public List<OrderItemResponseDTO> getItems() {
        return items;
    }

    public void setItems(List<OrderItemResponseDTO> items) {
        this.items = items;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        OrderResponseDTO that = (OrderResponseDTO) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(customerId, that.customerId) &&
                Objects.equals(orderDate, that.orderDate) &&
                Objects.equals(orderStatus, that.orderStatus) &&
                Objects.equals(items, that.items) &&
                Objects.equals(totalPrice, that.totalPrice);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, customerId, orderDate, orderStatus, items, totalPrice);
    }

    @Override
    public String toString() {
        return "OrderResponseDTO{" +
                "id=" + id +
                ", customerId='" + customerId + '\'' +
                ", orderDate=" + orderDate +
                ", orderStatus='" + orderStatus + '\'' +
                ", items=" + items +
                ", totalPrice=" + totalPrice +
                '}';
    }
}
