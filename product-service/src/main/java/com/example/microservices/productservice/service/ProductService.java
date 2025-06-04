package com.example.microservices.productservice.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.microservices.productservice.client.InventoryServiceClient;
import com.example.microservices.productservice.dto.InventoryRequestDTO;
import com.example.microservices.productservice.dto.InventoryResponseDTO;
import com.example.microservices.productservice.model.Product;
import com.example.microservices.productservice.repository.ProductRepository;

@Service
public class ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    private final ProductRepository productRepository;
    private final InventoryServiceClient inventoryServiceClient;

    public ProductService(ProductRepository productRepository, InventoryServiceClient inventoryServiceClient) {
        this.productRepository = productRepository;
        this.inventoryServiceClient = inventoryServiceClient;
    }

    @CacheEvict(value = "products", allEntries = true)
    @Transactional
    public Product createProduct(Product product) {
        if (product.getId() == null) {
            product.setId(UUID.randomUUID().toString());
        }
        logger.info("Creating product with id: {}", product.getId());
        Product savedProduct = productRepository.save(product);

        // Create inventory item via Feign client
        logger.info("Attempting to create inventory item for product ID: {}", savedProduct.getId());
        InventoryRequestDTO inventoryRequest = new InventoryRequestDTO(savedProduct.getId(), 0);

        try {
            ResponseEntity<InventoryResponseDTO> responseEntity = inventoryServiceClient
                    .createOrUpdateInventory(inventoryRequest);
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                logger.info("Successfully created/updated inventory item for product ID: {}. Response: {}",
                        savedProduct.getId(), responseEntity.getBody());
            } else {
                logger.error("Failed to create/update inventory item for product ID: {}. Status: {}, Body: {}",
                        savedProduct.getId(), responseEntity.getStatusCode(), responseEntity.getBody());
                throw new RuntimeException("Failed to create/update inventory item for product " + savedProduct.getId()
                        + ". Status: " + responseEntity.getStatusCode());
            }
        } catch (Exception e) {
            logger.error("Error calling inventory service for product ID: {}. Error: {}", savedProduct.getId(),
                    e.getMessage(), e);
            throw new RuntimeException("Failed to create inventory item for product " + savedProduct.getId()
                    + " due to communication error.", e);
        }

        return savedProduct;
    }

    @Cacheable(value = "products")
    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        logger.info("Fetching all products from database");
        return productRepository.findAll();
    }

    @Cacheable(value = "products", key = "#id")
    @Transactional(readOnly = true)
    public Optional<Product> getProductById(String id) {
        logger.info("Fetching product with id: {} from database", id);
        return productRepository.findById(id);
    }

    @CachePut(value = "products", key = "#id")
    @Transactional
    public Product updateProduct(String id, Product productDetails) {
        logger.info("Updating product with id: {}", id);
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id " + id));

        existingProduct.setName(productDetails.getName());
        existingProduct.setDescription(productDetails.getDescription());
        existingProduct.setPrice(productDetails.getPrice());
        return productRepository.save(existingProduct);
    }

    @CacheEvict(value = "products", key = "#id")
    @Transactional
    public void deleteProduct(String id) {
        logger.info("Deleting product with id: {}", id);
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Product not found with id " + id);
        }
        productRepository.deleteById(id);
    }

    @CacheEvict(value = "products", allEntries = true)
    public void refreshAllProductsCache() {
        logger.info("Evicting all entries from 'products' cache.");
    }
}
