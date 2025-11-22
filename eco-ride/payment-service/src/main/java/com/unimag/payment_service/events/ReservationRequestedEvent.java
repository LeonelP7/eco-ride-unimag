package com.unimag.payment_service.events;

public record ReservationRequestedEvent(
        String reservationId,
        String tripId,
        String passengerId,
        Double amount
) {
}
