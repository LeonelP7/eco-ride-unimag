package com.unimag.payment_service.dtos;

import com.unimag.payment_service.utils.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@AllArgsConstructor
@Data
public class PaymentResponseDTO {
    private final String paymentIntentId;
    private final String reserveId;
    private final PaymentStatus status;
    private final String provider;
    private final String providerRef;
    private final LocalDateTime capturedAt;

    public boolean isAuthorized() {
        return this.status == PaymentStatus.AUTHORIZED;
    }
}
