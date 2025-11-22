package com.unimag.passenger_service.services;

import com.unimag.passenger_service.dtos.rating.CreateRatingDTO;
import com.unimag.passenger_service.dtos.rating.ResponseRatingDTO;

import java.util.List;

public interface RatingService {

    ResponseRatingDTO createRating(CreateRatingDTO dto);

    ResponseRatingDTO getRatingById(String id);

    List<ResponseRatingDTO> getRatingsByPassengerId(String passengerId);

    List<ResponseRatingDTO> getRatingsByTripId(String tripId);

    List<ResponseRatingDTO> getAllRatings();

    void deleteRating(String id);
}