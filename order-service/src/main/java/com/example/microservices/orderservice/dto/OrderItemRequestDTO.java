package com.example.microservices.orderservice.dto;

import java.util.Objects;

public class OrderItemRequestDTO {
	private String productId; 
	private Integer quantity;

	public OrderItemRequestDTO() {
	}

	public OrderItemRequestDTO(String productId, Integer quantity) {
		this.productId = productId;
		this.quantity = quantity;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
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
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		OrderItemRequestDTO that = (OrderItemRequestDTO) o;
		return Objects.equals(productId, that.productId) &&
				Objects.equals(quantity, that.quantity);
	}

	@Override
	public int hashCode() {
		return Objects.hash(productId, quantity);
	}

	@Override
	public String toString() {
		return "OrderItemRequestDTO{" +
				"productId='" + productId + '\'' +
				", quantity=" + quantity +
				'}';
	}
}
