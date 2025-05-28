package com.edu.unimagdalena.inventoryservice.controller;

import com.edu.unimagdalena.inventoryservice.entity.Inventory;
import com.edu.unimagdalena.inventoryservice.service.InventoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = InventoryController.class)
public class InventoryControllerUnitTest {

    @Autowired
    private WebTestClient webTestClient;

    @Mock
    private InventoryService inventoryService;

    @InjectMocks
    private InventoryController inventoryController;

    private Inventory inventory1;
    private Inventory inventory2;
    private UUID inventory1Id;

    @BeforeEach
    void setUp() {
        inventory1Id = UUID.randomUUID();
        inventory1 = Inventory.builder().id(inventory1Id).product("Laptop").availableQuantity(10).build();
        inventory2 = Inventory.builder().id(UUID.randomUUID()).product("Mouse").availableQuantity(20).build();
    }

    @Test
    void getAllInventory_shouldReturnInventories() {
        when(inventoryService.getAllInventory()).thenReturn(Flux.just(inventory1, inventory2));

        webTestClient.get().uri("/api/v1/inventario")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Inventory.class).hasSize(2).contains(inventory1, inventory2);
    }

    @Test
    void getInventoryById_shouldReturnInventory_whenFound() {
        when(inventoryService.getInventoryById(inventory1Id)).thenReturn(Mono.just(inventory1));

        webTestClient.get().uri("/api/v1/inventario/{id}", inventory1Id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Inventory.class).isEqualTo(inventory1);
    }

    @Test
    void getInventoryById_shouldReturnNotFound_whenNotExists() {
        UUID nonExistentId = UUID.randomUUID();
        when(inventoryService.getInventoryById(nonExistentId)).thenReturn(Mono.empty());

        webTestClient.get().uri("/api/v1/inventario/{id}", nonExistentId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void createInventory_shouldCreateAndReturnInventory() {
        when(inventoryService.createInventory(any(Inventory.class))).thenReturn(Mono.just(inventory1));

        webTestClient.post().uri("/api/v1/inventario")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(inventory1), Inventory.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Inventory.class).isEqualTo(inventory1);
    }
}
