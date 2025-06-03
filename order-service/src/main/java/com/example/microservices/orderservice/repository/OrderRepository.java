package com.example.microservices.orderservice.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.microservices.orderservice.model.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
}
