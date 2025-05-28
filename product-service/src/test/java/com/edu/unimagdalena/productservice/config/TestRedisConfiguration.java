package com.edu.unimagdalena.productservice.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import redis.embedded.RedisServer;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.io.IOException;

@TestConfiguration
public class TestRedisConfiguration {

    private RedisServer redisServer;

    // Choose a port that is unlikely to conflict
    // You can also use a random port: SocketUtils.findAvailableTcpPort()
    private final int redisPort = 6370; 

    @PostConstruct
    public void startRedis() throws IOException {
        // Check if the port is available before starting
        // This is a simplified check; a more robust solution might involve trying a few ports
        // or using a library that guarantees an available port.
        try {
            redisServer = RedisServer.builder()
                .port(redisPort)
                .setting("maxmemory 128M") // Optional: configure as needed
                .build();
            redisServer.start();
        } catch (Exception e) {
            // Log or handle the error, e.g., if the port is already in use
            System.err.println("Failed to start embedded Redis: " + e.getMessage());
            // Optionally, rethrow or set a flag indicating Redis is not available for tests
        }
    }

    @PreDestroy
    public void stopRedis() {
        if (redisServer != null && redisServer.isActive()) {
            redisServer.stop();
        }
    }

    // Expose the port as a bean if needed by other configurations, though typically Spring Boot auto-configures
    // based on properties. This bean is mostly for programmatic access if required.
    @Bean
    public Integer redisPort() {
        return redisPort;
    }
}
