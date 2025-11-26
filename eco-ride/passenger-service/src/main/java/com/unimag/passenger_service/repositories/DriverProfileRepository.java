package com.unimag.passenger_service.repositories;

import com.unimag.passenger_service.entities.DriverProfile;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface DriverProfileRepository extends R2dbcRepository<DriverProfile, String> {

    // ✅ Buscar por passengerId (retorna Mono)
    Mono<DriverProfile> findByPassengerId(String passengerId);

    // ✅ Verificar si existe por passengerId
    Mono<Boolean> existsByPassengerId(String passengerId);
}