package com.unimag.payment_service.services;

import com.unimag.payment_service.dtos.PaymentResponseDTO;
import com.unimag.payment_service.events.ReservationRequestedEvent;

public interface PaymentService {

    PaymentResponseDTO processReservePayment(ReservationRequestedEvent event);
}
