package com.edu.unimagdalena.productservice.service;

import com.edu.unimagdalena.productservice.entity.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ProductService {
    Flux<Product> getAllProduct();
    Mono<Product> getProductById(UUID id);
    Mono<Product> createProduct(Product product);
    Mono<Product> updateProduct(UUID id, Product product);
    Mono<Product> deleteProduct(UUID id);
}
