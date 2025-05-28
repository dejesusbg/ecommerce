package com.edu.unimagdalena.apigateway;

import com.edu.unimagdalena.apigateway.controller.FallbackController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

@WebFluxTest(controllers = FallbackController.class)
public class FallbackControllerTests {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void defaultFallback_shouldReturnServiceUnavailable() {
        webTestClient.get().uri("/fallback/default")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(503)
                .expectBody()
                .jsonPath("$.error").isEqualTo("Service Unavailable")
                .jsonPath("$.message")
                .isEqualTo("The requested service is temporarily unavailable. Please try again later.");
    }
}
