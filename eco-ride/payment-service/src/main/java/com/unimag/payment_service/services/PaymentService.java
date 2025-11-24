package com.unimag.payment_service.services;

import com.unimag.payment_service.dtos.PaymentResponseDTO;
import com.unimag.payment_service.events.ReservationCancelledEvent;
import com.unimag.payment_service.events.ReservationRequestedEvent;
import reactor.core.publisher.Mono;

public interface PaymentService {

    Mono<PaymentResponseDTO> processReservationRequested(ReservationRequestedEvent event);

    Mono<Void> processReservationCancelled(ReservationCancelledEvent event);
}
