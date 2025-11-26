package com.unimag.passenger_service.dtos.rating;

import java.time.LocalDateTime;

public record ResponseRatingDTO(
        String id,
        String tripId,
        String fromPassengerId,  // Cambio: ID en lugar de nombre
        String toPassengerId,    // Cambio: ID en lugar de nombre
        Integer score,
        String comment,
        LocalDateTime createdAt
) {
}