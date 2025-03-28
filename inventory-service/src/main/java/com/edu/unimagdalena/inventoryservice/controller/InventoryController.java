package com.edu.unimagdalena.inventoryservice.controller;

import com.edu.unimagdalena.inventoryservice.entity.Inventory;
import com.edu.unimagdalena.inventoryservice.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.net.URL;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/inventario")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping
    public Flux<ResponseEntity<Inventory>> getAllInventory() {
        return inventoryService.getAllInventory()
                .map(ResponseEntity::ok);
        //.map(inventory -> ResponseEntity.status(HttpStatus.OK).body(inventory));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Inventory>> getInventoryById(@PathVariable UUID id) {
        return inventoryService.getInventoryById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<ResponseEntity<Inventory>> createInventory(@RequestBody Inventory inventory) {
        ServletUriComponentsBuilder location = ServletUriComponentsBuilder.fromCurrentRequest();
        return inventoryService.createInventory(inventory)
                .map(createInventory -> ResponseEntity
                        .created(location.path("/{id}")
                                .buildAndExpand(createInventory.getId())
                                .toUri())
                        .body(createInventory)
                );
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Inventory>> updateInventory(@PathVariable UUID id, @RequestBody Inventory inventory) {
        return inventoryService.updateInventory(id, inventory)
                .map(updateInventory -> ResponseEntity.ok(updateInventory))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Inventory>> deleteInventory(@PathVariable UUID id) {
        return inventoryService.deleteInventory(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
