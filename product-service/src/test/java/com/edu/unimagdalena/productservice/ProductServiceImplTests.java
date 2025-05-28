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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceImplTests {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ReactiveRedisTemplate<String, Product> redisTemplate; // Mocking Redis template, actual caching tests are in integration

    @InjectMocks
    private ProductServiceImpl productService;

    private Product product1;
    private Product product2;

    @BeforeEach
    void setUp() {
        product1 = Product.builder().id("1").name("Laptop Pro").description("High-performance laptop").price(1200.00).category("Electronics").build();
        product2 = Product.builder().id("2").name("Smartphone X").description("Latest model smartphone").price(800.00).category("Electronics").build();
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
        when(productRepository.findById(anyString())).thenReturn(Mono.just(product1));

        StepVerifier.create(productService.getProductById("1"))
            .expectNext(product1)
            .verifyComplete();

        verify(productRepository, times(1)).findById("1");
    }

    @Test
    void getProductById_shouldReturnEmpty_whenNotFound() {
        when(productRepository.findById(anyString())).thenReturn(Mono.empty());

        StepVerifier.create(productService.getProductById("3"))
            .verifyComplete();

        verify(productRepository, times(1)).findById("3");
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
        Product updatedProduct = Product.builder().id("1").name("Laptop Pro X").description("Updated laptop").price(1250.00).category("Electronics").build();
        when(productRepository.findById("1")).thenReturn(Mono.just(product1));
        when(productRepository.save(any(Product.class))).thenReturn(Mono.just(updatedProduct));

        StepVerifier.create(productService.updateProduct("1", updatedProduct))
            .expectNextMatches(p -> p.getName().equals("Laptop Pro X") && p.getPrice() == 1250.00)
            .verifyComplete();

        verify(productRepository, times(1)).findById("1");
        verify(productRepository, times(1)).save(any(Product.class)); // Verifies existingProduct was updated and saved
    }
    
    @Test
    void updateProduct_shouldReturnEmpty_whenNotFound() {
        Product updatedProduct = Product.builder().id("3").name("Laptop Pro X").description("Updated laptop").price(1250.00).category("Electronics").build();
        when(productRepository.findById("3")).thenReturn(Mono.empty());

        StepVerifier.create(productService.updateProduct("3", updatedProduct))
            .verifyComplete();

        verify(productRepository, times(1)).findById("3");
        verify(productRepository, never()).save(any(Product.class));
    }


    @Test
    void deleteProduct_shouldDeleteProduct_whenFound() {
        when(productRepository.findById("1")).thenReturn(Mono.just(product1));
        when(productRepository.delete(any(Product.class))).thenReturn(Mono.empty()); // delete returns Mono<Void>

        StepVerifier.create(productService.deleteProduct("1"))
            .expectNext(product1) // The service returns the product that was deleted
            .verifyComplete();

        verify(productRepository, times(1)).findById("1");
        verify(productRepository, times(1)).delete(product1);
    }
    
    @Test
    void deleteProduct_shouldReturnEmpty_whenNotFound() {
        when(productRepository.findById("3")).thenReturn(Mono.empty());

        StepVerifier.create(productService.deleteProduct("3"))
            .verifyComplete();

        verify(productRepository, times(1)).findById("3");
        verify(productRepository, never()).delete(any(Product.class));
    }
}
