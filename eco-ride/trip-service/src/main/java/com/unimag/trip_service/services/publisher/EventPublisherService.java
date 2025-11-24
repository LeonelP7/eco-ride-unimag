package com.unimag.trip_service.services.publisher;

import com.unimag.trip_service.config.RabbitConfig;
import com.unimag.trip_service.events.ReservationCancelledEvent;
import com.unimag.trip_service.events.ReservationConfirmedEvent;
import com.unimag.trip_service.events.ReservationRequestedEvent;
import com.unimag.trip_service.exceptions.eventExceptions.EventPublishException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventPublisherService {

    private final RabbitTemplate rabbitTemplate;

    public Mono<Void> publishReservationRequested(ReservationRequestedEvent event) {
        return Mono.fromRunnable(() -> {
            try {
                rabbitTemplate.convertAndSend(
                        RabbitConfig.EXCHANGE,
                        RabbitConfig.RK_RESERVATION_REQUESTED,
                        event
                );
                log.info("Published ReservationRequested: reservationId={}, tripId={}, passengerId={}, amount={}",
                        event.reservationId(), event.tripId(), event.passengerId(), event.amount());
            } catch (AmqpException e) {
                log.error("Failed to publish ReservationRequested event for reservation: {}",
                        event.reservationId(), e);
                throw new EventPublishException("Failed to publish reservation requested event", e);
            }
        }).subscribeOn(Schedulers.boundedElastic()).then(); // Usa scheduler para operaciones bloqueantes
    }

    public Mono<Void> publishReservationConfirmedEvent(ReservationConfirmedEvent event) {
        return Mono.fromRunnable(() -> {
            try {
                rabbitTemplate.convertAndSend(
                        RabbitConfig.EXCHANGE,
                        RabbitConfig.RK_RESERVATION_CONFIRMED,
                        event
                );
                log.info("Published ReservationConfirmed: reservationId={}", event.reservationId());
            } catch (AmqpException e) {
                log.error("Failed to publish ReservationConfirmed event for reservation: {}",
                        event.reservationId(), e);
                throw new EventPublishException("Failed to publish reservation confirmed event", e);
            }
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }

    public Mono<Void> publishReservationCancelledEvent(ReservationCancelledEvent event) {
        return Mono.fromRunnable(() -> {
            try {
                rabbitTemplate.convertAndSend(
                        RabbitConfig.EXCHANGE,
                        RabbitConfig.RK_RESERVATION_CANCELLED,
                        event
                );
                log.info("Published ReservationCancelled: reservationId={}, reason={}",
                        event.reservationId(), event.reason());
            } catch (AmqpException e) {
                log.error("Failed to publish ReservationCancelled event for reservation: {}",
                        event.reservationId(), e);
                throw new EventPublishException("Failed to publish reservation cancelled event", e);
            }
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }
}
