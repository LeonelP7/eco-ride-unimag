package com.unimag.trip_service.events;

public record PaymentAuthorized(
        String reservationId,
        String paymentIntedId,
        String chargeId
) {
}
