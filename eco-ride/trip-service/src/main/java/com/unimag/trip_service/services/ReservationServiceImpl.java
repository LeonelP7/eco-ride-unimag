package com.unimag.trip_service.services;

import com.unimag.trip_service.dtos.reservation.CreateReservationDTO;
import com.unimag.trip_service.dtos.reservation.ResponseReservationDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {



    @Override
    public ResponseReservationDTO registerReservation(CreateReservationDTO createReservationDTO) {
        return null;
    }

    @Override
    public List<ResponseReservationDTO> getReservations() {
        return List.of();
    }

    @Override
    public List<ResponseReservationDTO> getReservationsById(String id) {
        return List.of();
    }
}
