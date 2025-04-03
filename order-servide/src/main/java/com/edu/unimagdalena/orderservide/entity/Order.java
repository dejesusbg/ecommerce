package com.edu.unimagdalena.orderservide.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Builder
@Data
@Entity
@Table(name = "order")
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String customer;

    @ElementCollection
    @CollectionTable(name = "order_product", joinColumns = @JoinColumn(name = "order_id"))
    @MapKeyColumn(name = "product")
    @Column(name = "quantity")
    private Map<String, Integer> products = new HashMap<>();

    @Column(nullable = false)
    private String status;

    @Column(name = "creation_date", nullable = false)
    private LocalDateTime creationDate;
}