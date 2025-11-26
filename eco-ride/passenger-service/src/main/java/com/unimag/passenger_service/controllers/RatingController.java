package com.unimag.passenger_service.controllers;

import com.unimag.passenger_service.dtos.rating.CreateRatingDTO;
import com.unimag.passenger_service.dtos.rating.ResponseRatingDTO;
import com.unimag.passenger_service.services.RatingService;
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
public class RatingController {

    private final RatingService ratingService;

    @PostMapping
    public Mono<ResponseEntity<ResponseRatingDTO>> createRating(@Valid @RequestBody CreateRatingDTO dto) {
        return ratingService.createRating(dto)
                .map(rating -> ResponseEntity.status(HttpStatus.CREATED).body(rating));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<ResponseRatingDTO>> getRatingById(@PathVariable String id) {
        return ratingService.getRatingById(id)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/from/{fromId}")
    public Flux<ResponseRatingDTO> getRatingsByFromId(@PathVariable String fromId) {
        return ratingService.getRatingsByFromId(fromId);
    }

    @GetMapping("/to/{toId}")
    public Flux<ResponseRatingDTO> getRatingsByToId(@PathVariable String toId) {
        return ratingService.getRatingsByToId(toId);
    }

    @GetMapping("/trip/{tripId}")
    public Flux<ResponseRatingDTO> getRatingsByTripId(@PathVariable String tripId) {
        return ratingService.getRatingsByTripId(tripId);
    }

    @GetMapping
    public Flux<ResponseRatingDTO> getAllRatings() {
        return ratingService.getAllRatings();
    }
}