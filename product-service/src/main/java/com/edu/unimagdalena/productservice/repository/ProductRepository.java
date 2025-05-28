package com.edu.unimagdalena.productservice.repository;

import com.edu.unimagdalena.productservice.entity.Product;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends ReactiveMongoRepository<Product, String> { // Changed JpaRepository to ReactiveMongoRepository and UUID to String
}
