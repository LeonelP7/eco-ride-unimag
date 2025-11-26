package com.unimag.passenger_service.dtos.passenger;

import jakarta.validation.constraints.Email;

public record UpdatePassengerDTO(
        String name,

        @Email(message = "Email must be valid")
        String email,

        String phoneNumber
) {
}