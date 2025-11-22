package com.unimag.trip_service.services;

import com.unimag.trip_service.dtos.reservation.CreateReservationDTO;
import com.unimag.trip_service.dtos.reservation.ResponseReservationDTO;
import com.unimag.trip_service.entities.Reservation;
import com.unimag.trip_service.entities.Trip;
import com.unimag.trip_service.enums.ReservationStatus;
import com.unimag.trip_service.events.*;
import com.unimag.trip_service.exceptions.creationExceptions.ReservationCreationException;
import com.unimag.trip_service.exceptions.notfound.ReservationNotFoundException;
import com.unimag.trip_service.exceptions.notfound.TripNotFoundException;
import com.unimag.trip_service.mappers.ReservationMapper;
import com.unimag.trip_service.respositories.ReservationRepository;
import com.unimag.trip_service.respositories.TripRepository;
import com.unimag.trip_service.services.publisher.EventPublisherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final TripRepository tripRepository;
    private final ReservationMapper  reservationMapper;
    private final EventPublisherService eventPublisher;

    @Override
    public ResponseReservationDTO registerReservation(CreateReservationDTO createReservationDTO) {
        Trip trip = tripRepository.findById(createReservationDTO.tripId())
                .orElseThrow(() -> new TripNotFoundException("Trip not found"));

        if (trip.getSeatsAvailable() == 0){
            throw new ReservationCreationException("Seats not available");
        }else {
            trip.setSeatsAvailable(trip.getSeatsAvailable() - 1);
        }

        Reservation reservation = reservationMapper.createReservationDTOToReservation(createReservationDTO);
        reservation.setStatus(ReservationStatus.PENDING);
        reservation.setTrip(trip);
        trip.getReservations().add(reservation);
        tripRepository.save(trip);
        reservation = reservationRepository.save(reservation);

        log.info("Reservation created with status PENDING: {}", reservation.getId());

        try {
            eventPublisher.publishReservationRequested(new ReservationRequestedEvent(
                    reservation.getId(),
                    trip.getId(),
                    reservation.getPassengerId(),
                    trip.getPrice()
            ));
        } catch (Exception e) {
            log.error("Failed to publish ReservationRequested event", e);
            // En producción, considera guardar en outbox table para retry
        }

        return reservationMapper.reservationToResponseDTO(reservation);
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

    @Override
    public void processPaymentAuthorized(PaymentAuthorizedEvent event){
        log.info("ReservationService: Processing payment authorization for reservation: {}", event.reservationId());

        Reservation reservation = reservationRepository.findById(event.reservationId())
                .orElseThrow(() -> new ReservationNotFoundException("Reservation not found: " + event.reservationId()));

        reservation.setStatus(ReservationStatus.CONFIRMED);
        reservationRepository.save(reservation);

        log.info("Reservation confirmed: {}", reservation.getId());

        try {
            eventPublisher.publishReservationConfirmedEvent(
                    new ReservationConfirmedEvent(reservation.getId())
            );
        } catch (Exception e) {
            log.error("Failed to publish ReservationConfirmed event", e);
        }
    }

    @Override
    public void processPaymentFailed(PaymentFailedEvent event){
        log.info("Processing payment failure for reservation: {}", event.reservationId());

        Reservation reservation = reservationRepository.findById(event.reservationId())
                .orElseThrow(() -> new ReservationNotFoundException("Reservation not found: " + event.reservationId()));

        // COMPENSACIÓN: Devolver el asiento al inventario
        Trip trip = reservation.getTrip();
        trip.setSeatsAvailable(trip.getSeatsAvailable() + 1);
        tripRepository.save(trip);

        // Cancelar reservación
        reservation.setStatus(ReservationStatus.CANCELLED);
        reservationRepository.save(reservation);

        log.info("Reservation cancelled and seat returned. Reservation: {}, Reason: {}",
                reservation.getId(), event.reason());

        try {
            eventPublisher.publishReservationCancelledEvent(
                    new ReservationCancelledEvent(reservation.getId(), event.reason())
            );
        } catch (Exception e) {
            log.error("Failed to publish ReservationCancelled event", e);
        }
    }
}
