package com.unimag.notification_service.events;

public record ReservationConfirmedEvent(
        String reservationId,
        String email,
        String passengerName
) {
}
