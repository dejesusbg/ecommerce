package com.example.microservices.orderservice.dto;

import java.util.Objects;
import java.util.UUID;

public class OrderItemResponseDTO {
	private UUID id;
	private UUID productId;
	private Integer quantity;
	private Double price; 
	private String productName;

	public OrderItemResponseDTO() {
	}

	public OrderItemResponseDTO(UUID id, UUID productId, Integer quantity, Double price, String productName) {
		this.id = id;
		this.productId = productId;
		this.quantity = quantity;
		this.price = price;
		this.productName = productName;
	}

	// Getters and Setters
	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
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

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		OrderItemResponseDTO that = (OrderItemResponseDTO) o;
		return Objects.equals(id, that.id) &&
				Objects.equals(productId, that.productId) &&
				Objects.equals(quantity, that.quantity) &&
				Objects.equals(price, that.price) &&
				Objects.equals(productName, that.productName);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, productId, quantity, price, productName);
	}

	@Override
	public String toString() {
		return "OrderItemResponseDTO{" +
				"id=" + id +
				", productId=" + productId +
				", quantity=" + quantity +
				", price=" + price +
				", productName='" + productName + '\'' +
				'}';
	}
}
