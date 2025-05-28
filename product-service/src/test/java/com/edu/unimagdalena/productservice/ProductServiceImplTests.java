package com.edu.unimagdalena.productservice;

import com.edu.unimagdalena.productservice.entity.Product;
import com.edu.unimagdalena.productservice.repository.ProductRepository;
import com.edu.unimagdalena.productservice.service.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class ProductServiceImplTests {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ReactiveRedisTemplate<String, Product> redisTemplate; // Mocking Redis template, actual caching tests are in
                                                                  // integration

    @InjectMocks
    private ProductServiceImpl productService;

    private Product product1;
    private Product product2;

    @BeforeEach
    void setUp() {
        product1 = Product.builder().id(UUID.randomUUID()).name("Laptop Pro").description("High-performance laptop")
                .price(1200.00).category("Electronics").build();
        product2 = Product.builder().id(UUID.randomUUID()).name("Smartphone X").description("Latest model smartphone")
                .price(800.00).category("Electronics").build();
    }

    @Test
    void getAllProduct_shouldReturnAllProducts() {
        when(productRepository.findAll()).thenReturn(Flux.just(product1, product2));

        StepVerifier.create(productService.getAllProduct())
                .expectNext(product1)
                .expectNext(product2)
                .verifyComplete();

        verify(productRepository, times(1)).findAll();
    }

    @Test
    void getProductById_shouldReturnProduct_whenFound() {
        when(productRepository.findById(any(UUID.class))).thenReturn(Mono.just(product1));

        StepVerifier.create(productService.getProductById(product1.getId()))
                .expectNext(product1)
                .verifyComplete();

        verify(productRepository, times(1)).findById(product1.getId());
    }

    @Test
    void getProductById_shouldReturnEmpty_whenNotFound() {
        when(productRepository.findById(any(UUID.class))).thenReturn(Mono.empty());

        StepVerifier.create(productService.getProductById(UUID.randomUUID()))
                .verifyComplete();

        verify(productRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    void createProduct_shouldSaveAndReturnProduct() {
        when(productRepository.save(any(Product.class))).thenReturn(Mono.just(product1));

        StepVerifier.create(productService.createProduct(product1))
                .expectNext(product1)
                .verifyComplete();

        verify(productRepository, times(1)).save(product1);
    }

    @Test
    void updateProduct_shouldUpdateAndReturnProduct_whenFound() {
        Product updatedProduct = Product.builder().id(product1.getId()).name("Laptop Pro X")
                .description("Updated laptop")
                .price(1250.00).category("Electronics").build();
        when(productRepository.findById(product1.getId())).thenReturn(Mono.just(product1));
        when(productRepository.save(any(Product.class))).thenReturn(Mono.just(updatedProduct));

        StepVerifier.create(productService.updateProduct(product1.getId(), updatedProduct))
                .expectNextMatches(p -> p.getName().equals("Laptop Pro X") && p.getPrice() == 1250.00)
                .verifyComplete();

        verify(productRepository, times(1)).findById(product1.getId());
        verify(productRepository, times(1)).save(any(Product.class)); // Verifies existingProduct was updated and saved
    }

    @Test
    void updateProduct_shouldReturnEmpty_whenNotFound() {
        UUID nonExistentId = UUID.randomUUID();
        Product updatedProduct = Product.builder().id(nonExistentId).name("Laptop Pro X").description("Updated laptop")
                .price(1250.00).category("Electronics").build();
        when(productRepository.findById(nonExistentId)).thenReturn(Mono.empty());

        StepVerifier.create(productService.updateProduct(nonExistentId, updatedProduct))
                .verifyComplete();

        verify(productRepository, times(1)).findById(nonExistentId);
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void deleteProduct_shouldDeleteProduct_whenFound() {
        when(productRepository.findById(product1.getId())).thenReturn(Mono.just(product1));
        when(productRepository.delete(any(Product.class))).thenReturn(Mono.empty()); // delete returns Mono<Void>

        StepVerifier.create(productService.deleteProduct(product1.getId()))
                .expectNext(product1) // The service returns the product that was deleted
                .verifyComplete();

        verify(productRepository, times(1)).findById(product1.getId());
        verify(productRepository, times(1)).delete(product1);
    }

    @Test
    void deleteProduct_shouldReturnEmpty_whenNotFound() {
        UUID nonExistentId = UUID.randomUUID();
        when(productRepository.findById(nonExistentId)).thenReturn(Mono.empty());

        StepVerifier.create(productService.deleteProduct(nonExistentId))
                .verifyComplete();

        verify(productRepository, times(1)).findById(nonExistentId);
        verify(productRepository, never()).delete(any(Product.class));
    }
}
