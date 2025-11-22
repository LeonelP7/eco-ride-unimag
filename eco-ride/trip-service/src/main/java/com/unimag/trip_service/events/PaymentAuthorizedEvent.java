package com.unimag.trip_service.events;

public record PaymentAuthorizedEvent(
        String reservationId,
        String paymentIntedId,
        String chargeId
) {
}
