package com.unimag.passenger_service.repositories;

import com.unimag.passenger_service.entities.Rating;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RatingRepository extends R2dbcRepository<Rating, String> {

    // ✅ Buscar calificaciones dadas POR un pasajero
    Flux<Rating> findByFromId(String fromId);

    // ✅ Buscar calificaciones recibidas POR un pasajero
    Flux<Rating> findByToId(String toId);

    // ✅ Buscar por tripId
    Flux<Rating> findByTripId(String tripId);

    // ✅ Calcular promedio de calificaciones de un pasajero (query manual)
    @Query("SELECT AVG(score) FROM ratings WHERE to_id = :passengerId")
    Mono<Double> calculateAverageRatingForPassenger(@Param("passengerId") String passengerId);
}