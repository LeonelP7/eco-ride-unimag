package com.unimag.trip_service.services;

import com.unimag.trip_service.dtos.reservation.CreateReservationDTO;
import com.unimag.trip_service.dtos.reservation.ResponseReservationDTO;
import com.unimag.trip_service.entities.Reservation;
import com.unimag.trip_service.entities.Trip;
import com.unimag.trip_service.exceptions.notfound.ReservationNotFoundException;
import com.unimag.trip_service.exceptions.notfound.TripNotFoundException;
import com.unimag.trip_service.mappers.ReservationMapper;
import com.unimag.trip_service.respositories.ReservationRepository;
import com.unimag.trip_service.respositories.TripRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final TripRepository tripRepository;
    private final ReservationMapper  reservationMapper;

    @Override
    public ResponseReservationDTO registerReservation(CreateReservationDTO createReservationDTO) {
        Trip trip = tripRepository.findById(createReservationDTO.tripId())
                .orElseThrow(() -> new TripNotFoundException("Trip not found"));

        Reservation reservation = reservationMapper.createReservationDTOToReservation(createReservationDTO);
        reservation.setTrip(trip);

        return reservationMapper.reservationToResponseDTO(reservationRepository.save(reservation));
    }

    @Override
    public List<ResponseReservationDTO> findAll() {
        return reservationRepository.findAll()
                .stream()
                .map(reservationMapper::reservationToResponseDTO)
                .toList();
    }

    @Override
    public ResponseReservationDTO findById(String id) {
        return reservationRepository.findById(id)
                .map(reservationMapper::reservationToResponseDTO)
                .orElseThrow(() -> new ReservationNotFoundException("Reservation not found"));
    }
}
