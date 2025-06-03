package com.example.microservices.productservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.microservices.productservice.dto.InventoryRequestDTO;
import com.example.microservices.productservice.dto.InventoryResponseDTO;

@FeignClient(name = "inventory-service")
public interface InventoryServiceClient {

	@PostMapping("/api/inventory")
	ResponseEntity<InventoryResponseDTO> createOrUpdateInventory(@RequestBody InventoryRequestDTO request);
}
