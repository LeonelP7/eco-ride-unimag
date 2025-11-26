package com.unimag.trip_service.controllers;

import com.unimag.trip_service.dtos.reservation.CreateReservationDTO;
import com.unimag.trip_service.dtos.reservation.ResponseReservationDTO;
import com.unimag.trip_service.dtos.trip.CreateTripDTO;
import com.unimag.trip_service.dtos.trip.ResponseTripDTO;
import com.unimag.trip_service.services.ReservationService;
import com.unimag.trip_service.services.TripService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/trips")
@Tag(name = "Trip Management", description = "API for managing carpooling trips and reservations")
public class TripController {

    private final TripService tripService;
    private final ReservationService reservationService;

    @GetMapping
    @Operation(
            summary = "Search trips with filters",
            description = "Retrieves a list of trips filtered by origin, destination, and time range. All parameters are optional."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "List of trips retrieved successfully"
            )
    })
    public Flux<ResponseTripDTO> getTripsByFilters(
            @Parameter(description = "Trip origin location")
            @RequestParam(required = false) String origin,
            @Parameter(description = "Trip destination location")
            @RequestParam(required = false) String destination,
            @Parameter(description = "Start of time range (ISO 8601 format)", example = "2025-11-26T08:00:00")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @Parameter(description = "End of time range (ISO 8601 format)", example = "2025-11-26T18:00:00")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {

        return tripService.findByFilters(origin, destination, from, to);
    }

    @PostMapping
    @Operation(
            summary = "Create new trip",
            description = "Creates a new carpooling trip. Only users with ROLE_DRIVER can create trips."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Trip created successfully",
                    content = @Content(schema = @Schema(implementation = ResponseTripDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "User is not authorized to create trips (requires ROLE_DRIVER)"
            )
    })
    public Mono<ResponseEntity<ResponseTripDTO>> saveTrip(
            @Valid @RequestBody CreateTripDTO createTripDTO,
            @Parameter(description = "Driver ID (will be extracted from authentication token)")
            String driverId) {
        return tripService.saveTrip(createTripDTO, driverId)
                .map(trip -> ResponseEntity.status(HttpStatus.CREATED).body(trip));
    }

    @PostMapping("{tripId}/reservations")
    @Operation(
            summary = "Create reservation for trip",
            description = "Creates a new reservation for a specific trip. Only users with ROLE_PASSENGER can create reservations."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Reservation created successfully",
                    content = @Content(schema = @Schema(implementation = ResponseReservationDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data or no seats available"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Trip not found"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "User is not authorized to create reservations (requires ROLE_PASSENGER)"
            )
    })
    public Mono<ResponseEntity<ResponseReservationDTO>> saveReservation(
            @Parameter(description = "Trip ID", required = true)
            @PathVariable String tripId,
            @Valid @RequestBody CreateReservationDTO createReservationDTO) {
        return reservationService.registerReservation(createReservationDTO)
                .map(reservation -> ResponseEntity.status(HttpStatus.CREATED).body(reservation));
    }
}