package com.unimag.trip_service.services;

import com.unimag.trip_service.dtos.reservation.CreateReservationDTO;
import com.unimag.trip_service.dtos.reservation.ResponseReservationDTO;

import java.util.List;

public interface ReservationService {

    ResponseReservationDTO registerReservation(CreateReservationDTO createReservationDTO);
    List<ResponseReservationDTO> getReservations();
    List<ResponseReservationDTO> getReservationsById(String id);
}
