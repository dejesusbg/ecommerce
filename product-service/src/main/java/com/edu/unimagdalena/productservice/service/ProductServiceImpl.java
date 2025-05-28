package com.edu.unimagdalena.productservice.service;

import com.edu.unimagdalena.productservice.entity.Product;
import com.edu.unimagdalena.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public static final String PRODUCT_CACHE_KEY = "Product";

    @Override
    @Cacheable(value = PRODUCT_CACHE_KEY, key = "'all'")
    public Flux<Product> getAllProduct() {
        return productRepository.findAll();
    }

    @Override
    @Cacheable(value = PRODUCT_CACHE_KEY, key = "#id")
    public Mono<Product> getProductById(String id) {
        return productRepository.findById(id);
    }

    @Override
    @CacheEvict(value = PRODUCT_CACHE_KEY, allEntries = true)
    public Mono<Product> createProduct(Product product) {
        return productRepository.save(product);
    }

    @Override
    @CacheEvict(value = PRODUCT_CACHE_KEY, key = "#id")
    public Mono<Product> updateProduct(String id, Product product) {
        return productRepository.findById(id)
                .flatMap(existingProduct -> {
                    existingProduct.setName(product.getName());
                    existingProduct.setDescription(product.getDescription());
                    existingProduct.setPrice(product.getPrice());
                    existingProduct.setCategory(product.getCategory());
                    return productRepository.save(existingProduct);
                });
    }

    @Override
    @CacheEvict(value = PRODUCT_CACHE_KEY, key = "#id")
    public Mono<Product> deleteProduct(String id) {
        return productRepository.findById(id)
                .flatMap(existingProduct -> productRepository.delete(existingProduct).then(Mono.just(existingProduct)));
    }
}
