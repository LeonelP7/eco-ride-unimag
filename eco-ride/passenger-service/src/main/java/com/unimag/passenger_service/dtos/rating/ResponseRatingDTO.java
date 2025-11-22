package com.unimag.passenger_service.dtos.rating;

import java.time.LocalDateTime;

public record ResponseRatingDTO(
        String id,
        String tripId,
        String fromPassengerName,
        String toPassengerName,
        Integer score,
        String comment,
        LocalDateTime createdAt
) {
}