package com.unimag.passenger_service.events;

public record TripCompletedEvent(
        String tripId,
        String driverId,
        String passengerId
) {
}