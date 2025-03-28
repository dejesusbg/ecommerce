package com.edu.unimagdalena.inventoryservice.repository;

import com.edu.unimagdalena.inventoryservice.entity.Inventory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class InventoryRepositoryIntegrationTest {

    @Autowired
    private InventoryRepository inventoryRepository;

    @Test
    @DisplayName("Integración: Guardar inventario - Save")
    void integrationTestSaveInventory() {
        Inventory inventory = Inventory.builder()
                .product("Producto-H")
                .availableQuantity(12)
                .build();
        Inventory saved = inventoryRepository.save(inventory);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getProduct()).isEqualTo("Producto-H");
        assertThat(saved.getAvailableQuantity()).isEqualTo(12);
    }

    @Test
    @DisplayName("Integración: Buscar inventario por ID - findById")
    void integrationTestFindInventoryById() {
        Inventory inventory = Inventory.builder()
                .product("Producto-I")
                .availableQuantity(25)
                .build();
        Inventory saved = inventoryRepository.save(inventory);
        Optional<Inventory> found = inventoryRepository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getProduct()).isEqualTo("Producto-I");
    }

    @Test
    @DisplayName("Integración: Obtener todos los inventarios - findAll")
    void integrationTestFindAllInventories() {
        Inventory inv1 = Inventory.builder().product("Producto-J").availableQuantity(8).build();
        Inventory inv2 = Inventory.builder().product("Producto-K").availableQuantity(18).build();
        inventoryRepository.save(inv1);
        inventoryRepository.save(inv2);

        List<Inventory> inventories = inventoryRepository.findAll();
        assertThat(inventories)
                .hasSize(2)
                .extracting(Inventory::getProduct)
                .contains("Producto-J", "Producto-K");
    }

    @Test
    @DisplayName("Integración: Actualizar inventario - Update")
    void integrationTestUpdateInventory() {
        Inventory inventory = Inventory.builder()
                .product("Producto-L")
                .availableQuantity(40)
                .build();
        Inventory saved = inventoryRepository.save(inventory);

        saved.setAvailableQuantity(60);
        Inventory updated = inventoryRepository.save(saved);

        assertThat(updated.getAvailableQuantity()).isEqualTo(60);
    }

    @Test
    @DisplayName("Integración: Eliminar inventario - Delete")
    void integrationTestDeleteInventory() {
        Inventory inventory = Inventory.builder()
                .product("Producto-M")
                .availableQuantity(75)
                .build();
        Inventory saved = inventoryRepository.save(inventory);
        UUID id = saved.getId();

        inventoryRepository.delete(saved);
        Optional<Inventory> deleted = inventoryRepository.findById(id);

        assertThat(deleted).isNotPresent();
    }

    @Test
    @DisplayName("Integración: Verificar restricción de unicidad en producto")
    void integrationTestUniqueConstraintOnProduct() {
        Inventory inv1 = Inventory.builder().product("Producto-N").availableQuantity(30).build();
        inventoryRepository.save(inv1);

        Inventory inv2 = Inventory.builder().product("Producto-N").availableQuantity(45).build();
        assertThatThrownBy(() -> inventoryRepository.saveAndFlush(inv2))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}
