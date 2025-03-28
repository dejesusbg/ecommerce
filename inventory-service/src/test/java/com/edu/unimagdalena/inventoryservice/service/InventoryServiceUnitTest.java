package com.edu.unimagdalena.inventoryservice.service;

import com.edu.unimagdalena.inventoryservice.entity.Inventory;
import com.edu.unimagdalena.inventoryservice.repository.InventoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceUnitTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @InjectMocks
    private InventoryServiceImpl inventoryService;

    private Inventory sampleInventory;

    @BeforeEach
    void setUp() {
        sampleInventory = Inventory.builder()
                .id(UUID.randomUUID())
                .product("Producto-Test")
                .availableQuantity(100)
                .build();
    }

    @Test
    @DisplayName("Unit: Obtener todos los inventarios - getAllInventory")
    void testGetAllInventory() {
        when(inventoryRepository.findAll()).thenReturn(java.util.List.of(sampleInventory));
        Flux<Inventory> flux = inventoryService.getAllInventory();

        StepVerifier.create(flux)
                .expectNextMatches(inv -> inv.getProduct().equals("Producto-Test"))
                .verifyComplete();

        verify(inventoryRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Unit: Obtener inventario por ID - caso encontrado")
    void testGetInventoryByIdFound() {
        when(inventoryRepository.findById(sampleInventory.getId())).thenReturn(Optional.of(sampleInventory));
        Mono<Inventory> mono = inventoryService.getInventoryById(sampleInventory.getId());

        StepVerifier.create(mono)
                .assertNext(inv -> assertThat(inv.getProduct()).isEqualTo("Producto-Test"))
                .verifyComplete();

        verify(inventoryRepository, times(1)).findById(sampleInventory.getId());
    }

    @Test
    @DisplayName("Unit: Obtener inventario por ID - caso no encontrado")
    void testGetInventoryByIdNotFound() {
        UUID randomId = UUID.randomUUID();
        when(inventoryRepository.findById(randomId)).thenReturn(Optional.empty());
        Mono<Inventory> mono = inventoryService.getInventoryById(randomId);

        StepVerifier.create(mono)
                .expectNextCount(0)
                .verifyComplete();

        verify(inventoryRepository, times(1)).findById(randomId);
    }

    @Test
    @DisplayName("Unit: Crear inventario - createInventory")
    void testCreateInventory() {
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(sampleInventory);
        Mono<Inventory> mono = inventoryService.createInventory(sampleInventory);

        StepVerifier.create(mono)
                .assertNext(inv -> assertThat(inv.getAvailableQuantity()).isEqualTo(100))
                .verifyComplete();

        verify(inventoryRepository, times(1)).save(sampleInventory);
    }

    @Test
    @DisplayName("Unit: Actualizar inventario - caso encontrado")
    void testUpdateInventoryFound() {
        Inventory updatedData = Inventory.builder()
                .product("Producto-Test")
                .availableQuantity(200)
                .build();
        when(inventoryRepository.findById(sampleInventory.getId())).thenReturn(Optional.of(sampleInventory));
        Inventory updatedInventory = Inventory.builder()
                .id(sampleInventory.getId())
                .product("Producto-Test")
                .availableQuantity(200)
                .build();
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(updatedInventory);

        Mono<Inventory> mono = inventoryService.updateInventory(sampleInventory.getId(), updatedData);

        StepVerifier.create(mono)
                .assertNext(inv -> assertThat(inv.getAvailableQuantity()).isEqualTo(200))
                .verifyComplete();

        verify(inventoryRepository, times(1)).findById(sampleInventory.getId());
        verify(inventoryRepository, times(1)).save(any(Inventory.class));
    }

    @Test
    @DisplayName("Unit: Eliminar inventario - caso encontrado")
    void testDeleteInventoryFound() {
        when(inventoryRepository.findById(sampleInventory.getId()))
                .thenReturn(Optional.of(sampleInventory));
        doNothing().when(inventoryRepository).delete(sampleInventory);

        Mono<Inventory> mono = inventoryService.deleteInventory(sampleInventory.getId());

        StepVerifier.create(mono)
                .expectNextMatches(inv ->
                        inv.getId().equals(sampleInventory.getId()) &&
                                inv.getProduct().equals(sampleInventory.getProduct())
                )
                .verifyComplete();

        verify(inventoryRepository, times(1)).findById(sampleInventory.getId());
        verify(inventoryRepository, times(1)).delete(sampleInventory);
    }

    @Test
    @DisplayName("Unit: Eliminar inventario - caso no encontrado")
    void testDeleteInventoryNotFound() {
        UUID randomId = UUID.randomUUID();
        when(inventoryRepository.findById(randomId)).thenReturn(Optional.empty());

        Mono<Inventory> mono = inventoryService.deleteInventory(randomId);

        StepVerifier.create(mono)
                .verifyComplete();

        verify(inventoryRepository, times(1)).findById(randomId);
        verify(inventoryRepository, never()).delete(any());
    }

}
