package com.unimag.trip_service.services.publisher;

import com.unimag.trip_service.events.ReservationCancelledEvent;
import com.unimag.trip_service.events.ReservationConfirmedEvent;
import com.unimag.trip_service.events.ReservationRequestedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventPublisherService {

    private final RabbitTemplate rabbitTemplate;

    public void publishReservationRequested(ReservationRequestedEvent event) {
        try {
            rabbitTemplate.convertAndSend(
                    "reservation.topic",
                    "rservation.requested",
                    event
            );
            log.info("EVENT: ReservationRequested: {}", event.reservationId());
        } catch (Exception e) {
            log.error("ERROR publishing EVENT ReservationRequested", e);
            // Aquí podrían implementar reintentos o almacenamiento para envío posterior
        }
    }

    public void publishReservationConfirmedEvent(ReservationConfirmedEvent event) {
        try {
            rabbitTemplate.convertAndSend(
                    "reservation.topic",
                    "rservation.confirmed",
                    event
            );
            log.info("EVENT: ReservationConfirmed: {}", event.reservationId());
        } catch (Exception e) {
            log.error("ERROR publishing EVENT ReservationConfirmed", e);
            // Aquí podrían implementar reintentos o almacenamiento para envío posterior
        }
    }

    public void publishReservationCancelledEvent(ReservationCancelledEvent event) {
        try {
            rabbitTemplate.convertAndSend(
                    "reservation.topic",
                    "rservation.cancelled",
                    event
            );
            log.info("EVENT: ReservationCancelled: {}", event.reservationId());
        } catch (Exception e) {
            log.error("ERROR publishing EVENT ReservationCancelled", e);
            // Aquí podrían implementar reintentos o almacenamiento para envío posterior
        }
    }
}
