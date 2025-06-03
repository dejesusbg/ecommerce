package com.example.microservices.inventoryservice.controller;

import java.util.Optional;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.microservices.inventoryservice.dto.InventoryUpdateRequestDTO;
import com.example.microservices.inventoryservice.model.Inventory;
import com.example.microservices.inventoryservice.repository.InventoryRepository;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryRepository inventoryRepository;

    public InventoryController(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @GetMapping("/{productId}")
    public ResponseEntity<Integer> getStockQuantity(@PathVariable UUID productId) {
        Optional<Inventory> inventory = inventoryRepository.findByProductId(productId);
        return inventory.map(value -> ResponseEntity.ok(value.getQuantity()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Inventory> updateStock(@RequestBody InventoryUpdateRequestDTO request) {
        Optional<Inventory> optionalInventory = inventoryRepository.findByProductId(request.getProductId());
        Inventory inventory;
        if (optionalInventory.isPresent()) {
            inventory = optionalInventory.get();
            inventory.setQuantity(request.getNewQuantity());
        } else {
            inventory = new Inventory(request.getProductId(), request.getNewQuantity());
        }
        Inventory savedInventory = inventoryRepository.save(inventory);
        return new ResponseEntity<>(savedInventory, HttpStatus.OK);
    }
}
