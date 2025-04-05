package com.edu.unimagdalena.orderservide.repository;

import com.edu.unimagdalena.orderservide.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
}
