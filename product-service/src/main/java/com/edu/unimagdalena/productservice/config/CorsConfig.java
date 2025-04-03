package com.edu.unimagdalena.productservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("*");
    }

    /*
    * PARA PRODUCCCIÓN
    * .allowedOrigins("https://tudominio.com")
    * .allowedMethods("GET", "POST", "PUT")
    * */
}