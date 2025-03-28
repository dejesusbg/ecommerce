package com.edu.unimagdalena.inventoryservice.controller;

import com.edu.unimagdalena.inventoryservice.entity.Inventory;
import com.edu.unimagdalena.inventoryservice.service.InventoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryControllerTest {

    @Mock
    private InventoryService inventoryService;

    @InjectMocks
    private InventoryController inventoryController;

    private Inventory inventory;
    private UUID inventoryId;

    @BeforeEach
    void setUp() {
        inventoryId = UUID.randomUUID();
        inventory = Inventory.builder()
                .id(inventoryId)
                .product("Laptop")
                .availableQuantity(10)
                .build();
    }

    @Test
    void getAllInventory_ShouldReturnAllInventories() {
        Inventory inventory2 = Inventory.builder()
                .id(UUID.randomUUID())
                .product("Mouse")
                .availableQuantity(20)
                .build();

        when(inventoryService.getAllInventory()).thenReturn(Flux.just(inventory, inventory2));

        Flux<ResponseEntity<Inventory>> result = inventoryController.getAllInventory();

        StepVerifier.create(result)
                .expectNext(ResponseEntity.ok(inventory))
                .expectNext(ResponseEntity.ok(inventory2))
                .verifyComplete();
    }

    @Test
    void getInventoryById_WhenExists_ShouldReturnInventory() {
        when(inventoryService.getInventoryById(inventoryId)).thenReturn(Mono.just(inventory));

        Mono<ResponseEntity<Inventory>> result = inventoryController.getInventoryById(inventoryId);

        StepVerifier.create(result)
                .expectNext(ResponseEntity.ok(inventory))
                .verifyComplete();
    }

    @Test
    void getInventoryById_WhenNotExists_ShouldReturnNotFound() {
        when(inventoryService.getInventoryById(inventoryId)).thenReturn(Mono.empty());

        Mono<ResponseEntity<Inventory>> result = inventoryController.getInventoryById(inventoryId);

        StepVerifier.create(result)
                .expectNext(ResponseEntity.notFound().build())
                .verifyComplete();
    }

    @Test
    void createInventory_ShouldReturnCreatedInventory() {
        // Arrange
        Inventory newInventory = Inventory.builder()
                .product("Keyboard")
                .availableQuantity(15)
                .build();

        Inventory savedInventory = Inventory.builder()
                .id(inventoryId)
                .product("Keyboard")
                .availableQuantity(15)
                .build();

        when(inventoryService.createInventory(any(Inventory.class))).thenReturn(Mono.just(savedInventory));

        // Simular el contexto de solicitud HTTP
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setScheme("http");
        request.setServerName("localhost");
        request.setServerPort(8080);
        request.setRequestURI("/api/v1/inventario");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        // Act
        Mono<ResponseEntity<Inventory>> result = inventoryController.createInventory(newInventory);

        // Assert
        StepVerifier.create(result)
                .assertNext(response -> {
                    assert response.getStatusCode().is2xxSuccessful();
                    assert response.getBody() != null;
                    assert response.getBody().getId().equals(inventoryId);
                    assert response.getHeaders().getLocation() != null;
                    assert response.getHeaders().getLocation().toString()
                            .equals("http://localhost:8080/api/v1/inventario/" + inventoryId);
                })
                .verifyComplete();

        // Limpiar el contexto después de la prueba
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void updateInventory_WhenExists_ShouldReturnUpdatedInventory() {
        Inventory updatedInventory = Inventory.builder()
                .id(inventoryId)
                .product("Laptop Pro")
                .availableQuantity(5)
                .build();

        when(inventoryService.updateInventory(inventoryId, updatedInventory))
                .thenReturn(Mono.just(updatedInventory));

        Mono<ResponseEntity<Inventory>> result = inventoryController.updateInventory(inventoryId, updatedInventory);

        StepVerifier.create(result)
                .expectNext(ResponseEntity.ok(updatedInventory))
                .verifyComplete();
    }

    @Test
    void updateInventory_WhenNotExists_ShouldReturnNotFound() {
        Inventory updatedInventory = Inventory.builder()
                .id(inventoryId)
                .product("Laptop Pro")
                .availableQuantity(5)
                .build();

        when(inventoryService.updateInventory(inventoryId, updatedInventory))
                .thenReturn(Mono.empty());

        Mono<ResponseEntity<Inventory>> result = inventoryController.updateInventory(inventoryId, updatedInventory);

        StepVerifier.create(result)
                .expectNext(ResponseEntity.notFound().build())
                .verifyComplete();
    }

    @Test
    void deleteInventory_WhenExists_ShouldReturnOkWithDeletedInventory() {
        when(inventoryService.deleteInventory(inventoryId)).thenReturn(Mono.just(inventory));

        Mono<ResponseEntity<Inventory>> result = inventoryController.deleteInventory(inventoryId);

        StepVerifier.create(result)
                .expectNext(ResponseEntity.ok(inventory))
                .verifyComplete();
    }

    @Test
    void deleteInventory_WhenNotExists_ShouldReturnNotFound() {
        when(inventoryService.deleteInventory(inventoryId)).thenReturn(Mono.empty());

        Mono<ResponseEntity<Inventory>> result = inventoryController.deleteInventory(inventoryId);

        StepVerifier.create(result)
                .expectNext(ResponseEntity.notFound().build())
                .verifyComplete();
    }
}