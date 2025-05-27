package com.edu.unimagdalena.productservice.controller;

import com.edu.unimagdalena.productservice.entity.Product;
import com.edu.unimagdalena.productservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/producto")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    @PreAuthorize("hasRole('nuevo-rol')")
    public Flux<ResponseEntity<Product>> getAllProduct() {
        return productService.getAllProduct()
                .map(ResponseEntity::ok);
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Product>> getProductoById(@PathVariable UUID id) {
        return productService.getProductById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<ResponseEntity<Product>> createProduct(@RequestBody Product product) {
        ServletUriComponentsBuilder location = ServletUriComponentsBuilder.fromCurrentRequest();
        return productService.createProduct(product)
                .map(createProduct -> ResponseEntity
                        .created(
                                location.path("/{id}")
                                .buildAndExpand(createProduct.getId())
                                .toUri()
                        ).body(createProduct)
                );
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Product>> updateProduct(@PathVariable UUID id, @RequestBody Product product) {
        return productService.updateProduct(id, product)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Product>> deleteProduct(@PathVariable UUID id) {
        return productService.deleteProduct(id)
                .map(product -> ResponseEntity.status(HttpStatus.OK).body(product))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
