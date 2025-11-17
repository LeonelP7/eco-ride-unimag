package com.unimag.trip_service.dtos.reservation;

import com.unimag.trip_service.enums.ReservationStatus;

import java.time.LocalDateTime;

public record CreateReservationDTO(
    String tripId,
    String passengerId,
    ReservationStatus status,
    LocalDateTime createdAt
) {
}
