package com.edu.unimagdalena.productservice;

import com.edu.unimagdalena.productservice.entity.Product;
import com.edu.unimagdalena.productservice.repository.ProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataMongoTest // Uses an embedded MongoDB instance by default
public class ProductRepositoryIntegrationTests {

    @Autowired
    private ProductRepository productRepository;

    private Product product1;
    private Product product2;

    @BeforeEach
    void setUp() {
        // Clean up before each test to ensure a fresh state
        productRepository.deleteAll().block();

        product1 = Product.builder().name("Laptop Pro").description("High-performance laptop").price(1200.00)
                .category("Electronics").build();
        product2 = Product.builder().name("Smartphone X").description("Latest model smartphone").price(800.00)
                .category("Electronics").build();

        // Save initial data - block for setup
        productRepository.saveAll(Arrays.asList(product1, product2)).blockLast();
    }

    @AfterEach
    void tearDown() {
        productRepository.deleteAll().block();
    }

    @Test
    void save_shouldPersistProduct() {
        Product newProduct = Product.builder().name("Tablet Y").description("New tablet").price(300.00)
                .category("Electronics").build();

        StepVerifier.create(productRepository.save(newProduct))
                .assertNext(savedProduct -> {
                    assertNotNull(savedProduct.getId());
                    assertEquals("Tablet Y", savedProduct.getName());
                })
                .verifyComplete();
    }

    @Test
    void findById_shouldReturnProduct_whenExists() {
        // product1 should have an ID assigned after being saved in setUp
        // We need to fetch it again to get the ID or ensure IDs are set pre-save if
        // predictable
        // For this test, let's assume product1.getId() is valid after setUp's save.
        // A better way: save product1 and then use its ID.

        Mono<Product> foundProductMono = productRepository.findAll() // Find one of the saved products
                .filter(p -> p.getName().equals("Laptop Pro"))
                .next()
                .flatMap(p -> productRepository.findById(p.getId()));

        StepVerifier.create(foundProductMono)
                .assertNext(foundProduct -> {
                    assertEquals("Laptop Pro", foundProduct.getName());
                })
                .verifyComplete();
    }

    @Test
    void findById_shouldReturnEmpty_whenNotExists() {
        UUID nonExistentId = UUID.randomUUID();
        StepVerifier.create(productRepository.findById(nonExistentId))
                .verifyComplete();
    }

    @Test
    void findAll_shouldReturnAllProducts() {
        StepVerifier.create(productRepository.findAll().collectList())
                .assertNext(products -> {
                    assertEquals(2, products.size());
                    // Check if products with expected names are present
                    boolean hasLaptop = products.stream().anyMatch(p -> "Laptop Pro".equals(p.getName()));
                    boolean hasSmartphone = products.stream().anyMatch(p -> "Smartphone X".equals(p.getName()));
                    assertEquals(true, hasLaptop);
                    assertEquals(true, hasSmartphone);
                })
                .verifyComplete();
    }

    @Test
    void deleteById_shouldRemoveProduct() {
        Mono<Product> productToDeleteMono = productRepository.findAll()
                .filter(p -> p.getName().equals("Laptop Pro"))
                .next();

        Product productToDelete = productToDeleteMono.block(); // Block to get product for deletion
        assertNotNull(productToDelete);
        UUID idToDelete = productToDelete.getId();

        StepVerifier.create(productRepository.deleteById(idToDelete))
                .verifyComplete();

        StepVerifier.create(productRepository.findById(idToDelete))
                .verifyComplete(); // Expect empty as it's deleted
    }
}
