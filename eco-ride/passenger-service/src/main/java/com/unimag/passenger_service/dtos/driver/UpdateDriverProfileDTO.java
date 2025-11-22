package com.unimag.passenger_service.dtos.driver;

import jakarta.validation.constraints.Positive;

public record UpdateDriverProfileDTO(
        String carPlate,
        String carModel,
        String carColor,

        @Positive(message = "Seats must be positive")
        Integer seatsOffered
) {
}