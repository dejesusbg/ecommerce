package com.edu.unimagdalena.inventoryservice.service;

import com.edu.unimagdalena.inventoryservice.entity.Inventory;
import com.edu.unimagdalena.inventoryservice.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;

    @Override
    public Flux<Inventory> getAllInventory() {
        return Flux.defer(() -> Flux.fromIterable(inventoryRepository.findAll()))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Inventory> getInventoryById(UUID id) {
        return Mono.fromCallable(() -> inventoryRepository.findById(id).orElse(null))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Inventory> createInventory(Inventory inventory) {
        return Mono.fromCallable(() -> inventoryRepository.save(inventory))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Inventory> updateInventory(UUID id, Inventory inventory) {
        return getInventoryById(id)
                .flatMap(existing -> {
                    inventory.setId(existing.getId());
                    return Mono.fromCallable(() -> inventoryRepository.save(inventory))
                            .subscribeOn(Schedulers.boundedElastic());
                });
    }

    @Override
    public Mono<Inventory> deleteInventory(UUID id) {
        return getInventoryById(id)
                .flatMap(existing ->
                        Mono.fromCallable(() -> {
                            inventoryRepository.delete(existing);
                            return existing;
                        }).subscribeOn(Schedulers.boundedElastic())
                );
    }

}