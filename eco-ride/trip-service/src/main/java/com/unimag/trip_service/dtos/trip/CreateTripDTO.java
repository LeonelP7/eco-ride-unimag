package com.unimag.trip_service.dtos.trip;

import com.unimag.trip_service.enums.TripStatus;

import java.time.LocalDateTime;

public record CreateTripDTO(
        String origin,
        String destination,
        LocalDateTime startTime,
        int seatsTotal,
        int seatsAvailable,
        Double price,
        TripStatus status
) {
}
