package com.unimag.passenger_service.controllers;

import com.unimag.passenger_service.dtos.rating.CreateRatingDTO;
import com.unimag.passenger_service.dtos.rating.ResponseRatingDTO;
import com.unimag.passenger_service.services.RatingService;
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
@RequestMapping("api/v1/ratings")
@Tag(name = "Rating Management", description = "API for managing passenger and driver ratings")
public class RatingController {

    private final RatingService ratingService;

    @PostMapping
    @Operation(
            summary = "Create rating",
            description = "Creates a new rating for a completed trip"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Rating created successfully",
                    content = @Content(schema = @Schema(implementation = ResponseRatingDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Rating already exists for this trip"
            )
    })
    public Mono<ResponseEntity<ResponseRatingDTO>> createRating(
            @Valid @RequestBody CreateRatingDTO dto) {
        return ratingService.createRating(dto)
                .map(rating -> ResponseEntity.status(HttpStatus.CREATED).body(rating));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get rating by ID",
            description = "Retrieves a specific rating using its unique identifier"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Rating found",
                    content = @Content(schema = @Schema(implementation = ResponseRatingDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Rating not found"
            )
    })
    public Mono<ResponseEntity<ResponseRatingDTO>> getRatingById(
            @Parameter(description = "Rating ID", required = true)
            @PathVariable String id) {
        return ratingService.getRatingById(id)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/from/{fromId}")
    @Operation(
            summary = "Get ratings given by user",
            description = "Retrieves all ratings submitted by a specific user"
    )
    @ApiResponse(
            responseCode = "200",
            description = "List of ratings retrieved successfully"
    )
    public Flux<ResponseRatingDTO> getRatingsByFromId(
            @Parameter(description = "ID of the user who gave the ratings", required = true)
            @PathVariable String fromId) {
        return ratingService.getRatingsByFromId(fromId);
    }

    @GetMapping("/to/{toId}")
    @Operation(
            summary = "Get ratings received by user",
            description = "Retrieves all ratings received by a specific user"
    )
    @ApiResponse(
            responseCode = "200",
            description = "List of ratings retrieved successfully"
    )
    public Flux<ResponseRatingDTO> getRatingsByToId(
            @Parameter(description = "ID of the user who received the ratings", required = true)
            @PathVariable String toId) {
        return ratingService.getRatingsByToId(toId);
    }

    @GetMapping("/trip/{tripId}")
    @Operation(
            summary = "Get ratings for trip",
            description = "Retrieves all ratings associated with a specific trip"
    )
    @ApiResponse(
            responseCode = "200",
            description = "List of ratings retrieved successfully"
    )
    public Flux<ResponseRatingDTO> getRatingsByTripId(
            @Parameter(description = "Trip ID", required = true)
            @PathVariable String tripId) {
        return ratingService.getRatingsByTripId(tripId);
    }

    @GetMapping
    @Operation(
            summary = "Get all ratings",
            description = "Retrieves a list of all ratings in the system"
    )
    @ApiResponse(
            responseCode = "200",
            description = "List of ratings retrieved successfully"
    )
    public Flux<ResponseRatingDTO> getAllRatings() {
        return ratingService.getAllRatings();
    }
}