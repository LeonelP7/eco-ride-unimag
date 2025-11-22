package com.unimag.trip_service.services;

import com.unimag.trip_service.dtos.reservation.CreateReservationDTO;
import com.unimag.trip_service.dtos.reservation.ResponseReservationDTO;
import com.unimag.trip_service.events.PaymentAuthorizedEvent;
import com.unimag.trip_service.events.PaymentFailedEvent;

import java.util.List;

public interface ReservationService {

    ResponseReservationDTO registerReservation(CreateReservationDTO createReservationDTO);
    List<ResponseReservationDTO> findAll();
    ResponseReservationDTO findById(String id);
    void processPaymentAuthorized(PaymentAuthorizedEvent event);
    void processPaymentFailed(PaymentFailedEvent event);
}
