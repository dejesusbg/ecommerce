package com.edu.unimagdalena.servicediscovery.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ServiceConsumerController {

    private final RestTemplate restTemplate;

    @GetMapping("/consume-service")
    public String consumeService() {
        String serviceUrl = "http://eureka-client:8081/hostname";
        return restTemplate.getForObject(serviceUrl, String.class);
    }

}
