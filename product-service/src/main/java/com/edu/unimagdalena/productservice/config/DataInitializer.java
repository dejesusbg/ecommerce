package com.edu.unimagdalena.productservice.config;

import com.edu.unimagdalena.productservice.entity.Product;
import com.edu.unimagdalena.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final ProductRepository productRepository;

    @Override
    public void run(String... args) throws Exception {
        log.info("Starting data initialization...");
        productRepository.deleteAll() // Clear existing data
            .thenMany(
                Flux.fromIterable(getInitialProducts())
                    .flatMap(productRepository::save)
            )
            .doOnComplete(() -> log.info("Data initialization completed."))
            .doOnError(error -> log.error("Error during data initialization: ", error))
            .subscribe();
    }

    private List<Product> getInitialProducts() {
        return Arrays.asList(
            Product.builder().name("Laptop Pro").description("High-performance laptop").price(1200.00).category("Electronics").build(),
            Product.builder().name("Smartphone X").description("Latest model smartphone").price(800.00).category("Electronics").build(),
            Product.builder().name("Coffee Maker").description("Automatic coffee maker").price(150.00).category("Appliances").build(),
            Product.builder().name("Running Shoes").description("Comfortable running shoes").price(120.00).category("Footwear").build()
        );
    }
}
