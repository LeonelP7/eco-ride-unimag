package com.unimag.trip_service.events;

public record ReservationCancelledEvent(
        String reservationId,
        String reason
) {
}
