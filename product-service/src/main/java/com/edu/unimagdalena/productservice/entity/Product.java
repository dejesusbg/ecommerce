package com.edu.unimagdalena.productservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Builder
@Data
@Document(collection = "products")
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    private String id; // Changed to String for MongoDB ID

    private String name;

    private String description;

    private Double price;

    private String category;

}
