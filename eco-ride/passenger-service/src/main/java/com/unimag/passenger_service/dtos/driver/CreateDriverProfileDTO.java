package com.unimag.passenger_service.dtos.driver;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateDriverProfileDTO(
        @NotBlank(message = "Passenger ID is required")
        String passengerId,

        @NotBlank(message = "License number is required")
        String licenseNo,

        @NotBlank(message = "Car plate is required")
        String carPlate,

        String carModel,

        String carColor,

        @NotNull(message = "Seats offered is required")
        @Positive(message = "Seats must be positive")
        Integer seatsOffered
) {
}