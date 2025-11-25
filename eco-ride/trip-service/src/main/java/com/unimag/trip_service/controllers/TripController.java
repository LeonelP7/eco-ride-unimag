package com.unimag.trip_service.controllers;

import com.unimag.trip_service.dtos.reservation.CreateReservationDTO;
import com.unimag.trip_service.dtos.reservation.ResponseReservationDTO;
import com.unimag.trip_service.dtos.trip.CreateTripDTO;
import com.unimag.trip_service.dtos.trip.ResponseTripDTO;
import com.unimag.trip_service.entities.Trip;
import com.unimag.trip_service.services.ReservationService;
import com.unimag.trip_service.services.TripService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/trips")
public class TripController {

    private final TripService tripService;
    private final ReservationService reservationService;

    @GetMapping
    public Flux<ResponseTripDTO> getTripsByFilters(
            @RequestParam(required = false) String origin,
            @RequestParam(required = false) String destination,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {

        return tripService.findByFilters(origin, destination, from, to);
    }

    @PostMapping
    public Mono<ResponseEntity<ResponseTripDTO>> saveTrip(@RequestBody @Valid CreateTripDTO createTripDTO, String driverId
    ) {
        return tripService.saveTrip(createTripDTO, driverId)
                .map(dto -> ResponseEntity.status(HttpStatus.CREATED).body(dto));
    }

    // aqui hay que revisar esa pathVariable tripId
    @PostMapping("{tripId}/reservations")
    public Mono<ResponseEntity<ResponseReservationDTO>> saveReservation(@PathVariable String tripId, @RequestBody @Valid CreateReservationDTO createReservationDTO) {
        return reservationService.registerReservation(createReservationDTO)
                .map(dto -> ResponseEntity.status(HttpStatus.CREATED).body(dto));
    }

}
