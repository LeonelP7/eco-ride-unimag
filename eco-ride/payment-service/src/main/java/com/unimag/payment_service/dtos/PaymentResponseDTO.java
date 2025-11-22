package com.unimag.payment_service.dtos;

public record PaymentResponseDTO(
        String id,
        String reservationId,
        Double amount
) {
}
