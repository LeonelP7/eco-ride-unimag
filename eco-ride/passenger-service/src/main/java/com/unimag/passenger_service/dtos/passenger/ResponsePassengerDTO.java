package com.unimag.passenger_service.dtos.passenger;

import java.time.LocalDateTime;

public record ResponsePassengerDTO(
        String id,
        String name,
        String email,
        String phoneNumber,
        Double ratingAvg,
        Integer totalRatings,
        LocalDateTime createdAt
) {
}
