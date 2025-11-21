package com.unimag.payment_service.services;

import com.unimag.payment_service.dtos.PaymentResponseDTO;
import com.unimag.payment_service.events.ReservationRequestedEvent;
import com.unimag.payment_service.repositories.PaymentIntendRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentIntendRepository paymentIntendRepository;

    @Override
    public PaymentResponseDTO processReservationRequested(ReservationRequestedEvent event) {

        //logica de autorizar paymentInted



        return null;
    }
}
