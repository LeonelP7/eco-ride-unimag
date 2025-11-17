package com.unimag.trip_service.controllers;

import com.unimag.trip_service.dtos.trip.CreateTripDTO;
import com.unimag.trip_service.dtos.trip.ResponseTripDTO;
import com.unimag.trip_service.entities.Trip;
import com.unimag.trip_service.services.TripService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/trips")
public class TripController {

    private final TripService tripService;

    @GetMapping
    public ResponseEntity<List<ResponseTripDTO>> getTripsByFilters(
            @RequestParam(required = false) String origin,
            @RequestParam(required = false) String destination,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {

        List<ResponseTripDTO> trips = tripService.findByFilters(origin, destination, from, to);

        return ResponseEntity.ok(trips);
    }

    @PostMapping
    public ResponseEntity<ResponseTripDTO> saveTrip(@RequestBody @Valid CreateTripDTO createTripDTO, String driverId) {
        // falta añadir en el controller @AuthenticationPrincipal(expression = "id") Long driverId una vez se añada el security
        ResponseTripDTO trip = tripService.saveTrip(createTripDTO, driverId);
        return new ResponseEntity<>(trip, HttpStatus.CREATED);
    }

}
