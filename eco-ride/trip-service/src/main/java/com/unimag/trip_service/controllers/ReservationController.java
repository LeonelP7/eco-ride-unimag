package com.unimag.trip_service.controllers;

import com.unimag.trip_service.dtos.reservation.ResponseReservationDTO;
import com.unimag.trip_service.services.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    @GetMapping
    @Operation(
            summary = "Get all reservations",
            description = "Retrieves a list of all reservations in the system"
    )
    @ApiResponse(
            responseCode = "200",
            description = "List of reservations retrieved successfully"
    )
    public Flux<ResponseReservationDTO> getAll(){
        return reservationService.findAll();
    }

    @GetMapping("{id}")
    @Operation(
            summary = "Get reservation by ID",
            description = "Retrieves a specific reservation using its unique identifier"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Reservation found",
                    content = @Content(schema = @Schema(implementation = ResponseReservationDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Reservation not found"
            )
    })
    public Mono<ResponseEntity<ResponseReservationDTO>> getById(
            @Parameter(description = "Reservation ID", required = true)
            @PathVariable String id){
        return reservationService.findById(id)
                .map(ResponseEntity::ok);
    }
}
