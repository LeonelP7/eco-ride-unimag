package com.unimag.trip_service.events;

public record PaymentFailedEvent(
        String reservationId,
        String reason
) {
}
