package com.edu.unimagdalena.productservice.service;

import com.edu.unimagdalena.productservice.entity.Product;
import com.edu.unimagdalena.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public Flux<Product> getAllProduct() {
        return null;
    }

    @Override
    public Mono<Product> getProductById(UUID id) {
        return null;
    }

    @Override
    public Mono<Product> createProduct(Product product) {
        return null;
    }

    @Override
    public Mono<Product> updateProduct(UUID id, Product product) {
        return null;
    }

    @Override
    public Mono<Product> deleteProduct(UUID id) {
        return null;
    }
}
