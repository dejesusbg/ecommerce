package com.edu.unimagdalena.productservice.service;

import com.edu.unimagdalena.productservice.entity.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductService {
    Flux<Product> getAllProduct();
    Mono<Product> getProductById(String id); // Changed UUID to String
    Mono<Product> createProduct(Product product);
    Mono<Product> updateProduct(String id, Product product); // Changed UUID to String
    Mono<Product> deleteProduct(String id); // Changed UUID to String
}
