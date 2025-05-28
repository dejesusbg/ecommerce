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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

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

    // If using KeycloakRoleConverter in SecurityConfig, it might need to be a
    // @MockBean too
    // @MockBean
    // private KeycloakRoleConverter keycloakRoleConverter;

    private Product product1;
    private Product product2;

    @BeforeEach
    void setUp() {
        product1 = Product.builder().id("1").name("Laptop Pro").description("High-performance laptop").price(1200.00)
                .category("Electronics").build();
        product2 = Product.builder().id("2").name("Smartphone X").description("Latest model smartphone").price(800.00)
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
        when(productService.getProductById("1")).thenReturn(Mono.just(product1));

        webTestClient.get().uri("/api/v1/producto/{id}", "1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Product.class).isEqualTo(product1);
    }

    @Test
    void getProductoById_shouldReturnNotFound_whenNotExists() {
        when(productService.getProductById("3")).thenReturn(Mono.empty());

        webTestClient.get().uri("/api/v1/producto/{id}", "3")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void createProduct_shouldCreateAndReturnProduct() {
        when(productService.createProduct(any(Product.class))).thenReturn(Mono.just(product1));

        webTestClient.post().uri("/api/v1/producto")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(product1), Product.class)
                .exchange()
                .expectStatus().isCreated() // Matching the simplified response from controller
                .expectBody(Product.class).isEqualTo(product1);
    }

    @Test
    void updateProduct_shouldUpdateAndReturnProduct_whenFound() {
        Product updatedProduct = Product.builder().id("1").name("Laptop Pro X").description("Updated laptop")
                .price(1250.00).category("Electronics").build();
        when(productService.updateProduct(anyString(), any(Product.class))).thenReturn(Mono.just(updatedProduct));

        webTestClient.put().uri("/api/v1/producto/{id}", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(updatedProduct), Product.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Product.class).isEqualTo(updatedProduct);
    }

    @Test
    void updateProduct_shouldReturnNotFound_whenNotExists() {
        Product updatedProduct = Product.builder().id("3").name("Laptop Pro X").description("Updated laptop")
                .price(1250.00).category("Electronics").build();
        when(productService.updateProduct(anyString(), any(Product.class))).thenReturn(Mono.empty());

        webTestClient.put().uri("/api/v1/producto/{id}", "3")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(updatedProduct), Product.class)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void deleteProduct_shouldReturnOk_whenFound() {
        when(productService.deleteProduct("1")).thenReturn(Mono.just(product1)); // Assuming delete returns the deleted
                                                                                 // product

        webTestClient.delete().uri("/api/v1/producto/{id}", "1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Product.class).isEqualTo(product1); // Check if the deleted product is returned
    }

    @Test
    void deleteProduct_shouldReturnNotFound_whenNotExists() {
        when(productService.deleteProduct("3")).thenReturn(Mono.empty());

        webTestClient.delete().uri("/api/v1/producto/{id}", "3")
                .exchange()
                .expectStatus().isNotFound();
    }
}
