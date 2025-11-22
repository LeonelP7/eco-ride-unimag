package com.unimag.trip_service.events;

public record ReservationRequestedEvent(
        String reservationId,
        String tripId,
        String passengerId,
        Double amount
) {
}
