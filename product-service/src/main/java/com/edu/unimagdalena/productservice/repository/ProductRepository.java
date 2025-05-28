package com.edu.unimagdalena.productservice.repository;

import com.edu.unimagdalena.productservice.entity.Product;

import java.util.UUID;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends ReactiveMongoRepository<Product, UUID> {
}
