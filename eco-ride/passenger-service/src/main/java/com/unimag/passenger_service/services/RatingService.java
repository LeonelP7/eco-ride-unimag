package com.unimag.passenger_service.services;

import com.unimag.passenger_service.dtos.rating.CreateRatingDTO;
import com.unimag.passenger_service.dtos.rating.ResponseRatingDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RatingService {

    Mono<ResponseRatingDTO> createRating(CreateRatingDTO dto);

    Mono<ResponseRatingDTO> getRatingById(String id);

    Flux<ResponseRatingDTO> getRatingsByFromId(String fromId);

    Flux<ResponseRatingDTO> getRatingsByToId(String toId);

    Flux<ResponseRatingDTO> getRatingsByTripId(String tripId);

    Flux<ResponseRatingDTO> getAllRatings();
}