package com.unimag.payment_service.events;

public record ReservationCancelledEvent(
        String reservationId,
        String reason
) {
}
