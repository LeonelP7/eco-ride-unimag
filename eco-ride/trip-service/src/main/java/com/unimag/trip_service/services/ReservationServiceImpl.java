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
import com.unimag.trip_service.util.TripReservationPair;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final TripRepository tripRepository;
    private final ReservationMapper  reservationMapper;
    private final EventPublisherService eventPublisher;

    @Override
    public Mono<ResponseReservationDTO> registerReservation(CreateReservationDTO createReservationDTO) {
        return tripRepository.findById(createReservationDTO.tripId())
                .switchIfEmpty(Mono.error(new TripNotFoundException("Trip not found")))
                .flatMap(trip -> validateAndPrepareReservation(trip, createReservationDTO))
                .flatMap(this::saveReservation)
                .doOnSuccess(this::publishReservationRequestedEvent)
                .map(reservationMapper::reservationToResponseDTO);
    }

    private Mono<TripReservationPair> validateAndPrepareReservation(Trip trip, CreateReservationDTO dto) {
        if (trip.getSeatsAvailable() == 0) {
            return Mono.error(new ReservationCreationException("Seats not available"));
        }

        trip.setSeatsAvailable(trip.getSeatsAvailable() - 1);

        Reservation reservation = reservationMapper.createReservationDTOToReservation(dto);
        reservation.setStatus(ReservationStatus.PENDING);

        return Mono.just(new TripReservationPair(trip, reservation));
    }

    private Mono<Reservation> saveReservation(TripReservationPair pair) {
        Reservation reservation = pair.reservation();

        // Asignar ID manualmente
        reservation.setId(UUID.randomUUID().toString());

        return tripRepository.save(pair.trip())
                .then(reservationRepository.save(reservation))
                .doOnSuccess(r -> log.info("Reservation created with status PENDING: {}", r.getId()));
    }

    private Mono<Void> publishReservationRequestedEvent(Reservation reservation) {
        return tripRepository.findById(reservation.getTripId())
                .switchIfEmpty(Mono.error(new TripNotFoundException("Trip not found")))
                .flatMap(trip -> {
                    ReservationRequestedEvent event = new ReservationRequestedEvent(
                            reservation.getId(),
                            reservation.getTripId(),
                            reservation.getPassengerId(),
                            trip.getPrice()
                    );

                    eventPublisher.publishReservationRequested(event);
                    return Mono.<Void>empty();
                })
                .onErrorResume(e -> {
                    log.error("Failed to publish ReservationRequested event", e);
                    return Mono.empty();
                });
    }

    @Override
    public Flux<ResponseReservationDTO> findAll() {
        return reservationRepository.findAll()
                .map(reservationMapper::reservationToResponseDTO);
    }

    @Override
    public Mono<ResponseReservationDTO> findById(String id) {
        return reservationRepository.findById(id)
                .map(reservationMapper::reservationToResponseDTO)
                .switchIfEmpty(Mono.error(new ReservationNotFoundException("Reservation not found: " + id)));
    }

    @Override
    public void processPaymentAuthorized(PaymentAuthorizedEvent event) {
        log.info("ReservationService: Processing payment authorization for reservation: {}", event.reservationId());

        reservationRepository.findById(event.reservationId())
                .switchIfEmpty(Mono.error(new ReservationNotFoundException("Reservation not found: " + event.reservationId())))
                .flatMap(this::confirmReservation)
                .doOnSuccess(reservation -> publishConfirmationEvent(reservation.getId(), event.email(), event.passengerName()))
                .doOnError(e -> log.error("Error processing payment authorization", e))
                .subscribe();
    }

    private Mono<Reservation> confirmReservation(Reservation reservation) {
        reservation.setStatus(ReservationStatus.CONFIRMED);

        return reservationRepository.save(reservation)
                .doOnSuccess(r -> log.info("Reservation confirmed: {}", r.getId()));
    }

    private void publishConfirmationEvent(String reservationId, String email, String passengerName) {
        try {
            eventPublisher.publishReservationConfirmedEvent(
                    new ReservationConfirmedEvent(reservationId, email, passengerName)
            );
        } catch (Exception e) {
            log.error("Failed to publish ReservationConfirmed event", e);
        }
    }

    @Override
    public void processPaymentFailed(PaymentFailedEvent event) {
        log.info("Processing payment failure for reservation: {}", event.reservationId());

        reservationRepository.findById(event.reservationId())
                .switchIfEmpty(Mono.error(new ReservationNotFoundException("Reservation not found: " + event.reservationId())))
                .flatMap(this::compensateAndCancel)
                .doOnSuccess(reservation -> publishCancellationEvent(reservation, event.reason()))
                .doOnError(e -> log.error("Error processing payment failure", e))
                .subscribe();
    }

    private Mono<Reservation> compensateAndCancel(Reservation reservation) {

        return tripRepository.findById(reservation.getTripId())
                .switchIfEmpty(Mono.error(new IllegalStateException("Trip not found")))
                .flatMap(trip -> {
                    trip.setSeatsAvailable(trip.getSeatsAvailable() + 1);
                    reservation.setStatus(ReservationStatus.CANCELLED);

                    return tripRepository.save(trip)
                            .then(reservationRepository.save(reservation));
                })
                .doOnSuccess(r -> log.info("Reservation cancelled and seat returned: {}", r.getId()));
    }

    private void publishCancellationEvent(Reservation reservation, String reason) {
        try {
            eventPublisher.publishReservationCancelledEvent(
                    new ReservationCancelledEvent(reservation.getId(), reason)
            );
        } catch (Exception e) {
            log.error("Failed to publish ReservationCancelled event", e);
        }
    }
}
