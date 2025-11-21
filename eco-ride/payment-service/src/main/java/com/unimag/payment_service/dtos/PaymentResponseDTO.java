package com.unimag.payment_service.dtos;

import com.unimag.payment_service.enums.PaymentStatus;

import java.time.LocalDateTime;

public record PaymentResponseDTO(
        String paymentIntentId,
        String reserveId,
        PaymentStatus status,
        String provider,
        String providerRef,
        LocalDateTime capturedAt
) {
    public boolean isAuthorized() {
        return status == PaymentStatus.AUTHORIZED;
    }
}
