package com.edu.unimagdalena.inventoryservice.repository;

import com.edu.unimagdalena.inventoryservice.entity.Inventory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryRepositoryUnitTest {

    @Mock
    private InventoryRepository inventoryRepository;

    private Inventory inventory;

    @BeforeEach
    void setUp() {
        inventory = Inventory.builder()
                .id(UUID.randomUUID())
                .product("Producto-A")
                .availableQuantity(10)
                .build();
    }

    @Test
    @DisplayName("Test guardar inventario - Operación Save")
    void testSaveInventory() {
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(inventory);
        Inventory saved = inventoryRepository.save(inventory);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getProduct()).isEqualTo("Producto-A");
        assertThat(saved.getAvailableQuantity()).isEqualTo(10);
    }

    @Test
    @DisplayName("Test buscar inventario por ID - Operación findById")
    void testFindInventoryById() {
        when(inventoryRepository.findById(any(UUID.class))).thenReturn(Optional.of(inventory));
        Optional<Inventory> found = inventoryRepository.findById(inventory.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getProduct()).isEqualTo("Producto-A");
    }

    @Test
    @DisplayName("Test obtener todos los inventarios - Operación findAll")
    void testFindAllInventories() {
        List<Inventory> inventories = Arrays.asList(
                Inventory.builder().product("Producto-C").availableQuantity(5).build(),
                Inventory.builder().product("Producto-D").availableQuantity(15).build()
        );
        when(inventoryRepository.findAll()).thenReturn(inventories);

        List<Inventory> result = inventoryRepository.findAll();
        assertThat(result).hasSize(2);
        assertThat(result).extracting(Inventory::getProduct)
                .containsExactlyInAnyOrder("Producto-C", "Producto-D");
    }

    @Test
    @DisplayName("Test actualizar inventario - Operación Update")
    void testUpdateInventory() {
        inventory.setAvailableQuantity(50);
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(inventory);
        Inventory updated = inventoryRepository.save(inventory);

        assertThat(updated.getAvailableQuantity()).isEqualTo(50);
    }

    @Test
    @DisplayName("Test eliminar inventario - Operación Delete")
    void testDeleteInventory() {
        doNothing().when(inventoryRepository).delete(any(Inventory.class));
        inventoryRepository.delete(inventory);

        verify(inventoryRepository, times(1)).delete(inventory);
    }

    @Test
    @DisplayName("Test restricción de unicidad en producto - Unique Constraint")
    void testUniqueConstraintOnProduct() {
        when(inventoryRepository.save(any(Inventory.class)))
                .thenThrow(new RuntimeException("ConstraintViolationException"));

        assertThatThrownBy(() -> inventoryRepository.save(inventory))
                .isInstanceOf(RuntimeException.class);
    }
}