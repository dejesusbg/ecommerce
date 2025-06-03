package com.example.microservices.orderservice.dto;

import java.util.Objects;
import java.util.UUID;

public class InventoryDTO {
    private UUID productId;
    private Integer quantity;

    public InventoryDTO() {
    }

    public InventoryDTO(UUID productId, Integer quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InventoryDTO that = (InventoryDTO) o;
        return Objects.equals(productId, that.productId) &&
               Objects.equals(quantity, that.quantity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, quantity);
    }

    @Override
    public String toString() {
        return "InventoryDTO{" +
               "productId=" + productId +
               ", quantity=" + quantity +
               '}';
    }
}
