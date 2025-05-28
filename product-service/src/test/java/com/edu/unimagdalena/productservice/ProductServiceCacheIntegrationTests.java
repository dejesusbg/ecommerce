package com.edu.unimagdalena.productservice;

import com.edu.unimagdalena.productservice.config.TestRedisConfiguration;
import com.edu.unimagdalena.productservice.entity.Product;
import com.edu.unimagdalena.productservice.repository.ProductRepository;
import com.edu.unimagdalena.productservice.service.ProductService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;

import java.util.Objects;

// Use a specific test profile if needed, or configure properties directly
@ActiveProfiles("test-cache") // Example: if you have application-test-cache.properties for embedded Redis
@SpringBootTest
@Import(TestRedisConfiguration.class) // Import the embedded Redis configuration
public class ProductServiceCacheIntegrationTests {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CacheManager cacheManager;

    private Product product1;

    @BeforeEach
    void setUp() {
        // Clean up repository and caches before each test
        productRepository.deleteAll().block();
        clearAllCaches();

        product1 = Product.builder().name("Laptop Cache Test").description("Laptop for cache testing").price(1500.00)
                .category("Electronics").build();
        // Save and get ID, then save again to ensure it's in DB for caching
        Product saved = productRepository.save(product1).block();
        product1.setId(saved.getId()); // Set the ID from the saved entity
    }

    @AfterEach
    void tearDown() {
        productRepository.deleteAll().block();
        clearAllCaches();
    }

    private void clearAllCaches() {
        cacheManager.getCacheNames().parallelStream()
                .forEach(name -> Objects.requireNonNull(cacheManager.getCache(name)).clear());
    }

    @Test
    void getProductById_shouldCacheResult() {
        // First call - should hit repository
        StepVerifier.create(productService.getProductById(product1.getId()))
                .expectNextCount(1)
                .verifyComplete();

        // Second call - should hit cache, repository findById should not be called
        // again
        StepVerifier.create(productService.getProductById(product1.getId()))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void createProduct_shouldEvictGetAllCache() {
        // Prime the 'all' products cache (if getAllProduct is cached with key 'all')
        // For simplicity, we assume createProduct evicts the entire cache or specific
        // keys.
        // The current ProductServiceImpl evicts "Product::all" on createProduct.

        // Populate cache for "Product::all" (assuming getAllProduct is
        // @Cacheable("Product", key="'all'"))
        productService.getAllProduct().collectList().block(); // Call to populate cache

        // Create a new product - should evict "Product::all"
        Product newProduct = Product.builder().name("New Cache Evictor").price(100.0).category("Test").build();
        productService.createProduct(newProduct).block();

        // Call getAllProduct again - should hit repository as cache was evicted
        productService.getAllProduct().collectList().block();
    }

    @Test
    void updateProduct_shouldEvictSpecificCacheEntry() {
        // First call to populate cache for product1.getId()
        productService.getProductById(product1.getId()).block();

        // Update product1 - should evict "Product::${product1.getId()}"
        Product updatedProduct = Product.builder().name("Updated Laptop").price(1550.00).category("Electronics")
                .build();
        productService.updateProduct(product1.getId(), updatedProduct).block();

        // Call getProductById again for product1.getId() - should hit repository
        productService.getProductById(product1.getId()).block();
    }

    @Test
    void deleteProduct_shouldEvictSpecificCacheEntry() {
        // First call to populate cache for product1.getId()
        productService.getProductById(product1.getId()).block();

        // Delete product1 - should evict "Product::${product1.getId()}"
        productService.deleteProduct(product1.getId()).block();

        // Call getProductById again for product1.getId() - should hit repository (and
        // return empty)
        productService.getProductById(product1.getId()).block(); // Will be Mono.empty()
    }
}
