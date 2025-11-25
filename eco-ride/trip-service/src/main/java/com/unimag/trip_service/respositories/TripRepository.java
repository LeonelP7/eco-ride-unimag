package com.unimag.trip_service.respositories;

import com.unimag.trip_service.entities.Trip;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

public interface TripRepository extends R2dbcRepository<Trip, String> {

    @Query("""
    SELECT * FROM trips t
    WHERE (:origin IS NULL OR t.origin = :origin)
      AND (:destination IS NULL OR t.destination = :destination)
      AND (:from IS NULL OR t.start_time >= :from)
      AND (:to IS NULL OR t.start_time <= :to)
    """)
    Flux<Trip> findByFilters(
            @Param("origin") String origin,
            @Param("destination") String destination,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );

    Mono<Trip> getById(String tripId);

}
