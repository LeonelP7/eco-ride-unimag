package com.unimag.notification_service.dtos;

import lombok.Builder;

@Builder
public record ReservationBaseDTO(
        String reservationId,
        String email,
        String passengerName,
        String reason
) {
}
