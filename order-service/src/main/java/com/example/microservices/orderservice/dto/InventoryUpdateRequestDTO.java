package com.example.microservices.orderservice.dto;

import java.util.Objects;
import java.util.UUID;

public class InventoryUpdateRequestDTO {
    private UUID productId;
    private int newQuantity;

    public InventoryUpdateRequestDTO(UUID productId, int newQuantity) {
        this.productId = productId;
        this.newQuantity = newQuantity;
    }

    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public int getNewQuantity() {
        return newQuantity;
    }

    public void setNewQuantity(int newQuantity) {
        this.newQuantity = newQuantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        InventoryUpdateRequestDTO that = (InventoryUpdateRequestDTO) o;
        return Objects.equals(productId, that.productId) &&
                Objects.equals(newQuantity, that.newQuantity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, newQuantity);
    }

    @Override
    public String toString() {
        return "InventoryUpdateRequestDTO{" +
                "productId=" + productId +
                ", newQuantity=" + newQuantity +
                '}';
    }
}
