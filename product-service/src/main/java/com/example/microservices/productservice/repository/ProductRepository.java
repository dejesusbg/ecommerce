package com.example.microservices.productservice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.microservices.productservice.model.Product;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {
}
