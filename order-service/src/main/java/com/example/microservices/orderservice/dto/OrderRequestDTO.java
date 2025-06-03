package com.example.microservices.orderservice.dto;

import java.util.List;
import java.util.Objects;

public class OrderRequestDTO {
    private String customerId;
    private List<OrderItemRequestDTO> items;

    public OrderRequestDTO() {
    }

    public OrderRequestDTO(String customerId, List<OrderItemRequestDTO> items) {
        this.customerId = customerId;
        this.items = items;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public List<OrderItemRequestDTO> getItems() {
        return items;
    }

    public void setItems(List<OrderItemRequestDTO> items) {
        this.items = items;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        OrderRequestDTO that = (OrderRequestDTO) o;
        return Objects.equals(customerId, that.customerId) &&
                Objects.equals(items, that.items);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerId, items);
    }

    @Override
    public String toString() {
        return "OrderRequestDTO{" +
                "customerId='" + customerId + '\'' +
                ", items=" + items +
                '}';
    }
}
