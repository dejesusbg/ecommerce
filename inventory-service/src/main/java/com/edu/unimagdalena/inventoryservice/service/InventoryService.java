package com.edu.unimagdalena.inventoryservice.service;

import com.edu.unimagdalena.inventoryservice.entity.Inventory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface InventoryService {
    Flux<Inventory> getAllInventory();
    Mono<Inventory> getInventoryById(UUID id);
    Mono<Inventory> createInventory(Inventory inventory);
    Mono<Inventory> updateInventory(UUID id, Inventory inventory);
    Mono<Inventory> deleteInventory(UUID id);
}
