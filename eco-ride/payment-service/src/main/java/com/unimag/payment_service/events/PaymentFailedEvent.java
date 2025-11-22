package com.unimag.payment_service.events;

public record PaymentFailedEvent(
        String reservationId,
        String reason
) {
}
