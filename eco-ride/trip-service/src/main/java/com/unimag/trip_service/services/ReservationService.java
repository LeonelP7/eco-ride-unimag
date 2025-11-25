package com.unimag.trip_service.services;

import com.unimag.trip_service.dtos.reservation.CreateReservationDTO;
import com.unimag.trip_service.dtos.reservation.ResponseReservationDTO;
import com.unimag.trip_service.events.PaymentAuthorizedEvent;
import com.unimag.trip_service.events.PaymentFailedEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ReservationService {

    Mono<ResponseReservationDTO> registerReservation(CreateReservationDTO createReservationDTO);
    Flux<ResponseReservationDTO> findAll();
    Mono<ResponseReservationDTO> findById(String id);
    Mono<Void> processPaymentAuthorized(PaymentAuthorizedEvent event);
    Mono<Void> processPaymentFailed(PaymentFailedEvent event);
}
