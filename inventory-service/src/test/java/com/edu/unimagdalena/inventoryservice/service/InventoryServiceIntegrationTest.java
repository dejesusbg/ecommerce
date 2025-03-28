package com.edu.unimagdalena.inventoryservice.service;

import com.edu.unimagdalena.inventoryservice.entity.Inventory;
import com.edu.unimagdalena.inventoryservice.repository.InventoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class InventoryServiceIntegrationTest {

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private InventoryRepository inventoryRepository;

    private Inventory sampleInventory;

    @BeforeEach
    void setUp() {
        inventoryRepository.deleteAll();
        sampleInventory = Inventory.builder()
                .product("Producto-Integration")
                .availableQuantity(100)
                .build();
    }

    @Test
    @DisplayName("Integration: Obtener todos los inventarios - getAllInventory")
    void integrationTestGetAllInventory() {
        inventoryRepository.save(sampleInventory);
        Flux<Inventory> flux = inventoryService.getAllInventory();

        StepVerifier.create(flux)
                .expectNextMatches(inv -> inv.getProduct().equals("Producto-Integration"))
                .verifyComplete();
    }

    @Test
    @DisplayName("Integration: Obtener inventario por ID - caso encontrado")
    void integrationTestGetInventoryByIdFound() {
        Inventory saved = inventoryRepository.save(sampleInventory);
        Mono<Inventory> mono = inventoryService.getInventoryById(saved.getId());

        StepVerifier.create(mono)
                .assertNext(inv -> {
                    assertThat(inv.getProduct()).isEqualTo("Producto-Integration");
                    assertThat(inv.getAvailableQuantity()).isEqualTo(100);
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Integration: Obtener inventario por ID - caso no encontrado")
    void integrationTestGetInventoryByIdNotFound() {
        Mono<Inventory> mono = inventoryService.getInventoryById(UUID.randomUUID());

        StepVerifier.create(mono)
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    @DisplayName("Integration: Crear inventario - createInventory")
    void integrationTestCreateInventory() {
        Mono<Inventory> mono = inventoryService.createInventory(sampleInventory);

        StepVerifier.create(mono)
                .assertNext(inv -> {
                    assertThat(inv.getId()).isNotNull();
                    assertThat(inv.getProduct()).isEqualTo("Producto-Integration");
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Integration: Actualizar inventario - caso encontrado")
    void integrationTestUpdateInventoryFound() {
        Inventory saved = inventoryRepository.save(sampleInventory);
        Inventory updateData = Inventory.builder()
                .product("Producto-Integration")
                .availableQuantity(150)
                .build();
        Mono<Inventory> mono = inventoryService.updateInventory(saved.getId(), updateData);

        StepVerifier.create(mono)
                .assertNext(inv -> assertThat(inv.getAvailableQuantity()).isEqualTo(150))
                .verifyComplete();
    }

    @Test
    @DisplayName("Integration: Eliminar inventario - caso encontrado")
    void integrationTestDeleteInventoryFound() {
        Inventory saved = inventoryRepository.save(sampleInventory);

        StepVerifier.create(inventoryService.deleteInventory(saved.getId()))
                .expectNextMatches(deleted ->
                        deleted.getId().equals(saved.getId()) &&
                                deleted.getProduct().equals(saved.getProduct())
                )
                .verifyComplete();
    }

    @Test
    @DisplayName("Integration: Eliminar inventario - caso no encontrado")
    void integrationTestDeleteInventoryNotFound() {
        UUID nonExistentId = UUID.randomUUID();

        StepVerifier.create(inventoryService.deleteInventory(nonExistentId))
                .verifyComplete(); // O .expectError() dependiendo de tu implementación
    }

}
