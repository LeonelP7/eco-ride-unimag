package com.unimag.notification_service.events;

public record ReservationCancelledEvent(
        String reservationId,
        String email,
        String passengerName,
        String reason
) {
}
