package com.edu.unimagdalena.apigateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@RestController
public class FallbackController {

    @GetMapping("/fallback/default")
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public Mono<Map<String, String>> defaultFallback() {
        Map<String, String> response = new HashMap<>();
        response.put("error", "Service Unavailable");
        response.put("message", "The requested service is temporarily unavailable. Please try again later.");
        // It's good practice to include the correlation ID in fallbacks if possible,
        // but that requires accessing it from the request, which can be done via ServerWebExchange.
        // For simplicity in this example, it's omitted.
        return Mono.just(response);
    }
}
