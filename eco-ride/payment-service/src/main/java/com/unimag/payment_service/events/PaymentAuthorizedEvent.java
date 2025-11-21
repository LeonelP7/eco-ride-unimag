package com.unimag.payment_service.events;

import java.time.LocalDateTime;

public record PaymentAuthorizedEvent(
        String reserveId,
        String paymentIntentId,
        String provider,
        String providerRef,
        LocalDateTime capturedAt
) {
}
