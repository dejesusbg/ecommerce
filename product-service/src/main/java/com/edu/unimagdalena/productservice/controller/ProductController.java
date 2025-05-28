package com.edu.unimagdalena.productservice.controller;

import com.edu.unimagdalena.productservice.entity.Product;
import com.edu.unimagdalena.productservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("api/v1/producto")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    @PreAuthorize("hasRole('ROLE_MASTER') or hasRole('ROLE_ADMIN')")
    public Flux<ResponseEntity<Product>> getAllProduct() {
        return productService.getAllProduct()
                .map(ResponseEntity::ok);
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Product>> getProductoById(@PathVariable String id) { // Changed UUID to String
        return productService.getProductById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<ResponseEntity<Product>> createProduct(@RequestBody Product product) {
        // For reactive, location building needs to be handled differently or omitted if
        // not strictly necessary for the response
        // For simplicity, we'll return the created product without the Location header
        // for now.
        // If Location header is critical, it requires more complex handling with
        // ServerWebExchange.
        return productService.createProduct(product)
                .map(createdProduct -> ResponseEntity.status(HttpStatus.CREATED).body(createdProduct));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Product>> updateProduct(@PathVariable String id, @RequestBody Product product) { // Changed
                                                                                                                // UUID
                                                                                                                // to
                                                                                                                // String
        return productService.updateProduct(id, product)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Product>> deleteProduct(@PathVariable String id) { // Changed UUID to String
        return productService.deleteProduct(id)
                .map(deletedProduct -> ResponseEntity.status(HttpStatus.OK).body(deletedProduct)) // Ensure deleted
                                                                                                  // product is returned
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
