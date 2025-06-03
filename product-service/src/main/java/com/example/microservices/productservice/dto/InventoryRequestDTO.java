package com.example.microservices.productservice.dto;

public class InventoryRequestDTO {
    private String productId;
    private int quantity;

    public InventoryRequestDTO() {
    }

    public InventoryRequestDTO(String productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
