package com.unimag.passenger_service.repositories;

import com.unimag.passenger_service.entities.Passenger;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface PassengerRepository extends R2dbcRepository<Passenger, String> {

    // ✅ Buscar por email (retorna Mono)
    Mono<Passenger> findByEmail(String email);

    // ✅ Buscar por keycloakSub (retorna Mono)
    Mono<Passenger> findByKeycloakSub(String keycloakSub);

    // ✅ Verificar si existe por email (retorna Mono<Boolean>)
    Mono<Boolean> existsByEmail(String email);
}