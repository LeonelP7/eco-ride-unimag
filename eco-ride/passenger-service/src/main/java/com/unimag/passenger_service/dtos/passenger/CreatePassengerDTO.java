package com.unimag.passenger_service.dtos.passenger;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreatePassengerDTO(
        @NotBlank(message = "Keycloak sub is required")
        String keycloakSub,

        @NotBlank(message = "Name is required")
        String name,

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        String email,

        String phoneNumber
) {
}
