package com.edu.unimagdalena.productservice;

import com.edu.unimagdalena.productservice.controller.ProductController;
import com.edu.unimagdalena.productservice.entity.Product;
import com.edu.unimagdalena.productservice.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.UUID;

@WebFluxTest(controllers = ProductController.class)
// If SecurityConfig is interfering, you might need to mock it or import
// specific test configuration
// @Import(SecurityConfig.class) // Or a test specific security config
public class ProductControllerTests {

        @Autowired
        private WebTestClient webTestClient;

        @Mock
        private ProductService productService;

        @InjectMocks
        private ProductController productController;

        private Product product1;
        private Product product2;

        @BeforeEach
        void setUp() {
                product1 = Product.builder().id(UUID.randomUUID()).name("Laptop Pro")
                                .description("High-performance laptop").price(1200.00)
                                .category("Electronics").build();
                product2 = Product.builder().id(UUID.randomUUID()).name("Smartphone X")
                                .description("Latest model smartphone").price(800.00)
                                .category("Electronics").build();
        }

        @Test
        void getAllProduct_shouldReturnProducts() {
                when(productService.getAllProduct()).thenReturn(Flux.just(product1, product2));

                webTestClient.get().uri("/api/v1/producto")
                                .accept(MediaType.APPLICATION_JSON)
                                .exchange()
                                .expectStatus().isOk()
                                .expectBodyList(Product.class).hasSize(2).contains(product1, product2);
        }

        @Test
        void getProductoById_shouldReturnProduct_whenFound() {
                when(productService.getProductById(product1.getId())).thenReturn(Mono.just(product1));

                webTestClient.get().uri("/api/v1/producto/{id}", product1.getId().toString())
                                .accept(MediaType.APPLICATION_JSON)
                                .exchange()
                                .expectStatus().isOk()
                                .expectBody(Product.class).isEqualTo(product1);
        }

        @Test
        void getProductoById_shouldReturnNotFound_whenNotExists() {
                when(productService.getProductById(product2.getId())).thenReturn(Mono.empty());

                webTestClient.get().uri("/api/v1/producto/{id}", product2.getId().toString())
                                .accept(MediaType.APPLICATION_JSON)
                                .exchange()
                                .expectStatus().isNotFound();
        }

        @Test
        void createProduct_shouldCreateAndReturnProduct() {
                when(productService.createProduct(any(Product.class))).thenReturn(Mono.just(product1));

                // Matching the simplified response from controller
                webTestClient.post().uri("/api/v1/producto")
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(Mono.just(product1), Product.class)
                                .exchange()
                                .expectStatus().isCreated()
                                .expectBody(Product.class).isEqualTo(product1);
        }

        @Test
        void updateProduct_shouldUpdateAndReturnProduct_whenFound() {
                Product updatedProduct = Product.builder().id(product1.getId()).name("Laptop Pro X")
                                .description("Updated laptop")
                                .price(1250.00).category("Electronics").build();
                when(productService.updateProduct(any(UUID.class), any(Product.class)))
                                .thenReturn(Mono.just(updatedProduct));

                webTestClient.put().uri("/api/v1/producto/{id}", product1.getId().toString())
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(Mono.just(updatedProduct), Product.class)
                                .exchange()
                                .expectStatus().isOk()
                                .expectBody(Product.class).isEqualTo(updatedProduct);
        }

        @Test
        void updateProduct_shouldReturnNotFound_whenNotExists() {
                UUID nonExistentId = UUID.randomUUID();
                Product updatedProduct = Product.builder().id(nonExistentId).name("Laptop Pro X")
                                .description("Updated laptop")
                                .price(1250.00).category("Electronics").build();
                when(productService.updateProduct(any(UUID.class), any(Product.class))).thenReturn(Mono.empty());

                webTestClient.put().uri("/api/v1/producto/{id}", nonExistentId.toString())
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(Mono.just(updatedProduct), Product.class)
                                .exchange()
                                .expectStatus().isNotFound();
        }

        @Test
        void deleteProduct_shouldReturnOk_whenFound() {
                // Assuming delete returns the deleted product
                when(productService.deleteProduct(product1.getId())).thenReturn(Mono.just(product1));

                // Check if the deleted product is returned
                webTestClient.delete().uri("/api/v1/producto/{id}", product1.getId().toString())
                                .exchange()
                                .expectStatus().isOk()
                                .expectBody(Product.class).isEqualTo(product1);
        }

        @Test
        void deleteProduct_shouldReturnNotFound_whenNotExists() {
                UUID nonExistentId = UUID.randomUUID();
                when(productService.deleteProduct(nonExistentId)).thenReturn(Mono.empty());
                webTestClient.delete().uri("/api/v1/producto/{id}", nonExistentId.toString())
                                .exchange()
                                .expectStatus().isNotFound();
        }
}
