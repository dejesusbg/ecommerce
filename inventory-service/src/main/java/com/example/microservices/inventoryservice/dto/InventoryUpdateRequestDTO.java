package com.example.microservices.inventoryservice.dto;

import java.util.UUID;

public class InventoryUpdateRequestDTO {
    private UUID productId;
    private int newQuantity;

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
}