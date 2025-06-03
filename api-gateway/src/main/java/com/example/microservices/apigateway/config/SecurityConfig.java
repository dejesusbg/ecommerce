package com.example.microservices.apigateway.config;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;

import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

  @Bean
  public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
    http.authorizeExchange(exchanges -> exchanges
        // Allow public access to these endpoints
        .pathMatchers("/eureka/**", "/actuator/**", "/fallback/**").permitAll()
        // Secure product service endpoints
        .pathMatchers(org.springframework.http.HttpMethod.GET, "/api/products/**")
        .hasAnyRole("USER", "ADMIN")
        .pathMatchers(org.springframework.http.HttpMethod.POST, "/api/products/**")
        .hasRole("ADMIN")
        .pathMatchers(org.springframework.http.HttpMethod.PUT, "/api/products/**")
        .hasRole("ADMIN")
        .pathMatchers(org.springframework.http.HttpMethod.DELETE, "/api/products/**")
        .hasRole("ADMIN")
        // Secure other user service endpoints
        .pathMatchers("/api/orders/**", "/api/inventory/**", "/api/payments/**").hasAnyRole("USER", "ADMIN")
        // All other exchanges must be authenticated
        .anyExchange().authenticated())
        .oauth2ResourceServer(oauth2 -> oauth2
            .jwt(jwt -> jwt.jwtAuthenticationConverter(grantedAuthoritiesExtractor())));

    http.csrf(ServerHttpSecurity.CsrfSpec::disable);
    return http.build();
  }

  // Converter to extract roles from the JWT token
  Converter<Jwt, Mono<AbstractAuthenticationToken>> grantedAuthoritiesExtractor() {
    JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();

    jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwt -> {
      Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
      Collection<GrantedAuthority> authorities = new java.util.ArrayList<>();

      if (realmAccess != null) {
        Object rolesObj = realmAccess.get("roles");

        if (rolesObj instanceof List<?>) {
          List<?> rolesList = (List<?>) rolesObj;
          List<String> roles = rolesList.stream()
              .filter(String.class::isInstance)
              .map(String.class::cast)
              .collect(Collectors.toList());

          if (!roles.isEmpty()) {
            authorities.addAll(roles.stream()
                .map(roleName -> "ROLE_" + roleName.toUpperCase()) // Prefix with ROLE_
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList()));
          }
        }
      }

      // Fallback or check resource_access if realm_access is not found or empty
      Map<String, Object> resourceAccess = jwt.getClaimAsMap("resource_access");
      if (resourceAccess != null) {
        resourceAccess.forEach((clientId, clientData) -> {
          if (clientData instanceof Map<?, ?>) {
            Map<String, Object> clientAccessMap = ((Map<?, ?>) clientData).entrySet().stream()
                .collect(Collectors.toMap(
                    e -> String.valueOf(e.getKey()),
                    Map.Entry::getValue));

            Object rolesObj2 = clientAccessMap.get("roles");

            if (rolesObj2 instanceof List<?>) {
              List<?> rolesList2 = (List<?>) rolesObj2;
              List<String> roles = rolesList2.stream()
                  .filter(String.class::isInstance)
                  .map(String.class::cast)
                  .collect(Collectors.toList());

              if (!roles.isEmpty()) {
                authorities.addAll(roles.stream()
                    .map(roleName -> "ROLE_" + clientId.toUpperCase() + "_"
                        + roleName.toUpperCase())
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList()));
              }
            }
          }
        });
      }

      if (authorities.isEmpty()) {
        return List.of(); 
      }
      
      return authorities;
    });

    return new ReactiveJwtAuthenticationConverterAdapter(jwtAuthenticationConverter);
  }
}
