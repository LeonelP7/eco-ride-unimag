package com.unimag.passenger_service.dtos.rating;

import jakarta.validation.constraints.*;

public record CreateRatingDTO(
        @NotBlank(message = "Trip ID is required")
        String tripId,

        @NotBlank(message = "From passenger ID is required")
        String fromPassengerId,

        @NotBlank(message = "To passenger ID is required")
        String toPassengerId,

        @NotNull(message = "Score is required")
        @Min(value = 1, message = "Score must be at least 1")
        @Max(value = 5, message = "Score must be at most 5")
        Integer score,

        @Size(max = 500, message = "Comment must not exceed 500 characters")
        String comment
) {
}
