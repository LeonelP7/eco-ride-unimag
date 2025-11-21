package com.unimag.payment_service.events;

import java.time.LocalDateTime;

public record PaymentAuthorizedEvent(
        String reservationId,
        String paymentIntedId,
        String chargeId
) {
}
