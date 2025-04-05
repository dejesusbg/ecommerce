package com.edu.unimagdalena.productservice.service;

import com.edu.unimagdalena.productservice.entity.Product;
import com.edu.unimagdalena.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public Flux<Product> getAllProduct() {
        return Flux.defer(() -> Flux.fromIterable(productRepository.findAll()))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Product> getProductById(UUID id) {
        return Mono.fromCallable(() -> productRepository.findById(id).orElse(null))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Product> createProduct(Product product) {
        return Mono.fromCallable(() -> productRepository.save(product))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Product> updateProduct(UUID id, Product product) {
        return getProductById(id)
                .flatMap(existing -> {
                    product.setId(existing.getId());
                    return Mono.fromCallable(() -> productRepository.save(product))
                            .subscribeOn(Schedulers.boundedElastic());
                });
    }

    @Override
    public Mono<Product> deleteProduct(UUID id) {
        return getProductById(id)
                .flatMap(existing ->
                    Mono.fromCallable(() -> {
                        productRepository.delete(existing);
                       return existing;
                    }).subscribeOn(Schedulers.boundedElastic())
                );
    }
}
