package com.unimag.passenger_service.controllers;

import com.unimag.passenger_service.dtos.passenger.CreatePassengerDTO;
import com.unimag.passenger_service.dtos.passenger.ResponsePassengerDTO;
import com.unimag.passenger_service.dtos.passenger.UpdatePassengerDTO;
import com.unimag.passenger_service.services.PassengerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/passengers")
@Tag(name = "Passenger Management", description = "API for managing passenger profiles and information")
public class PassengerController {

    private final PassengerService passengerService;

    @PostMapping
    @Operation(
            summary = "Create passenger",
            description = "Creates a new passenger profile in the system"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Passenger created successfully",
                    content = @Content(schema = @Schema(implementation = ResponsePassengerDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Passenger already exists"
            )
    })
    public Mono<ResponseEntity<ResponsePassengerDTO>> createPassenger(
            @Valid @RequestBody CreatePassengerDTO dto) {
        return passengerService.createPassenger(dto)
                .map(passenger -> ResponseEntity.status(HttpStatus.CREATED).body(passenger));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get passenger by ID",
            description = "Retrieves a specific passenger using their unique identifier"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Passenger found",
                    content = @Content(schema = @Schema(implementation = ResponsePassengerDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Passenger not found"
            )
    })
    public Mono<ResponseEntity<ResponsePassengerDTO>> getPassengerById(
            @Parameter(description = "Passenger ID", required = true)
            @PathVariable String id) {
        return passengerService.getPassengerById(id)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/email/{email}")
    @Operation(
            summary = "Get passenger by email",
            description = "Retrieves a passenger using their email address"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Passenger found",
                    content = @Content(schema = @Schema(implementation = ResponsePassengerDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Passenger not found"
            )
    })
    public Mono<ResponseEntity<ResponsePassengerDTO>> getPassengerByEmail(
            @Parameter(description = "Passenger email address", required = true)
            @PathVariable String email) {
        return passengerService.getPassengerByEmail(email)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/keycloak/{keycloakSub}")
    @Operation(
            summary = "Get passenger by Keycloak subject",
            description = "Retrieves a passenger using their Keycloak subject identifier"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Passenger found",
                    content = @Content(schema = @Schema(implementation = ResponsePassengerDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Passenger not found"
            )
    })
    public Mono<ResponseEntity<ResponsePassengerDTO>> getPassengerByKeycloakSub(
            @Parameter(description = "Keycloak subject ID", required = true)
            @PathVariable String keycloakSub) {
        return passengerService.getPassengerByKeycloakSub(keycloakSub)
                .map(ResponseEntity::ok);
    }

    @GetMapping
    @Operation(
            summary = "Get all passengers",
            description = "Retrieves a list of all passengers in the system"
    )
    @ApiResponse(
            responseCode = "200",
            description = "List of passengers retrieved successfully"
    )
    public Flux<ResponsePassengerDTO> getAllPassengers() {
        return passengerService.getAllPassengers();
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update passenger",
            description = "Updates an existing passenger profile with new information"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Passenger updated successfully",
                    content = @Content(schema = @Schema(implementation = ResponsePassengerDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Passenger not found"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data"
            )
    })
    public Mono<ResponseEntity<ResponsePassengerDTO>> updatePassenger(
            @Parameter(description = "Passenger ID", required = true)
            @PathVariable String id,
            @Valid @RequestBody UpdatePassengerDTO dto) {
        return passengerService.updatePassenger(id, dto)
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete passenger",
            description = "Permanently deletes a passenger from the system"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Passenger deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Passenger not found"
            )
    })
    public Mono<ResponseEntity<Void>> deletePassenger(
            @Parameter(description = "Passenger ID", required = true)
            @PathVariable String id) {
        return passengerService.deletePassenger(id)
                .then(Mono.just(ResponseEntity.noContent().<Void>build()));
    }
}