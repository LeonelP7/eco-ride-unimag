package com.unimag.trip_service.services;

import com.unimag.trip_service.dtos.trip.CreateTripDTO;
import com.unimag.trip_service.dtos.trip.ResponseTripDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

public interface TripService {

    Mono<ResponseTripDTO> saveTrip(CreateTripDTO createTripDTO, String driverId);
    Flux<ResponseTripDTO> getTrips();
    Flux<ResponseTripDTO> findByFilters(String origin, String destination, LocalDateTime from, LocalDateTime to);
}
