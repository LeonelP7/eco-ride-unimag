package com.unimag.apigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        // Actuator endpoints
                        .pathMatchers("/actuator/**").permitAll()

                        // OAuth2 Login endpoints
                        .pathMatchers("/login/**", "/oauth2/**").permitAll()

                        // TripService endpoints
                        .pathMatchers(HttpMethod.POST, "/api/v1/trips").hasRole("DRIVER")
                        .pathMatchers(HttpMethod.GET, "/api/v1/trips").hasAnyRole("PASSENGER", "DRIVER")
                        .pathMatchers(HttpMethod.GET, "/api/v1/trips/**").hasAnyRole("PASSENGER", "DRIVER")
                        .pathMatchers(HttpMethod.POST, "/api/v1/trips/*/reservations").hasRole("PASSENGER")
                        .pathMatchers(HttpMethod.GET, "/api/v1/reservations/**").hasAnyRole("PASSENGER", "DRIVER")

                        // PassengerService endpoints
                        .pathMatchers("/api/v1/passengers/**").hasAnyRole("PASSENGER", "DRIVER", "ADMIN")
                        .pathMatchers("/api/v1/drivers/**").hasAnyRole("DRIVER", "ADMIN")
                        .pathMatchers("/api/v1/ratings/**").hasAnyRole("PASSENGER", "DRIVER")

                        // Require authentication for all other requests
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .jwtAuthenticationConverter(reactiveJwtAuthenticationConverter())
                        )
                );

        return http.build();
    }

    @Bean
    public Converter<Jwt, Mono<AbstractAuthenticationToken>> reactiveJwtAuthenticationConverter() {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new KeycloakRoleConverter());

        return new ReactiveJwtAuthenticationConverterAdapter(jwtAuthenticationConverter);
    }
}
