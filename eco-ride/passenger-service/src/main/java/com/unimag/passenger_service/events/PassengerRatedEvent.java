package com.unimag.passenger_service.events;

public record PassengerRatedEvent(
        String passengerId,
        Double newRatingAvg,
        Integer totalRatings
) {
}