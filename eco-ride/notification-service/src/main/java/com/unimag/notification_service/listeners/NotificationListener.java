package com.unimag.notification_service.listeners;

import com.unimag.notification_service.config.RabbitConfig;
import com.unimag.notification_service.dtos.ReservationBaseDTO;
import com.unimag.notification_service.enums.Templates;
import com.unimag.notification_service.events.ReservationConfirmedEvent;
import com.unimag.notification_service.events.ReservationCancelledEvent;
import com.unimag.notification_service.exceptions.NotificationNotSentException;
import com.unimag.notification_service.services.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationListener {
    private final NotificationService notificationService;
    private final Set<String> processedEvents = ConcurrentHashMap.newKeySet();

    @RabbitListener(queues = RabbitConfig.QUEUE_RESERVATION_CONFIRMED)
    public void onReservationConfirmed(ReservationConfirmedEvent event) {
        processReservationConfirmed(event)
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe(
                        result -> log.info("NotificationListener: Successfully processed ReservationConfirmed: {}", event.reservationId()),
                        error -> log.error("NotificationListener: Error in reactive pipeline for ReservationConfirmed: {}", error.getMessage())
                );
    }

    private Mono<Void> processReservationConfirmed(ReservationConfirmedEvent event) {
        return Mono.fromRunnable(() ->
                        log.info("NotificationListener: received ReservationConfirmed: reservationId={}", event.reservationId()))
                .then(Mono.defer(() -> {
                    String eventKey = "reservation-confirmed-" + event.reservationId();

                    if (!processedEvents.add(eventKey)) {
                        log.warn("NotificationListener: Event already processed, skipping: {}", eventKey);
                        return Mono.empty();
                    }

                    ReservationBaseDTO reservationBaseDTO = ReservationBaseDTO.builder()
                            .reservationId(event.reservationId())
                            .passengerName(event.passengerName())
                            .email(event.email())
                            .build();

                    return notificationService.createNotification(reservationBaseDTO,
                                    Templates.RESERVATION_CONFIRMED.getCode())
                            .doOnSuccess(v -> log.info("NotificationListener: Notification sent for reservation: {}",
                                    event.reservationId()))
                            .then()
                            .onErrorResume(ex -> {
                                log.error("NotificationListener: Error processing ReservationConfirmed for reservation {}: {}",
                                        event.reservationId(), ex.getMessage(), ex);

                                processedEvents.remove(eventKey);

                                if (ex instanceof NotificationNotSentException) {
                                    return Mono.error(new AmqpRejectAndDontRequeueException(
                                            "NotificationListener: Notification not sent, sending to DLQ", ex));
                                }

                                return Mono.error(ex);
                            });
                }));
    }

    @RabbitListener(queues = RabbitConfig.QUEUE_RESERVATION_CANCELLED)
    public void onReservationCancelled(ReservationCancelledEvent event) {
        processReservationCancelled(event)
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe(
                        result -> log.info("NotificationListener: Successfully processed ReservationCancelled: {}", event.reservationId()),
                        error -> log.error("NotificationListener: Error in reactive pipeline for ReservationCancelled: {}", error.getMessage())
                );
    }

    private Mono<Void> processReservationCancelled(ReservationCancelledEvent event) {
        return Mono.fromRunnable(() ->
                        log.info("NotificationListener: received ReservationCancelled: reservationId={}", event.reservationId()))
                .then(Mono.defer(() -> {
                    String eventKey = "reservation-cancelled-" + event.reservationId();

                    if (!processedEvents.add(eventKey)) {
                        log.warn("NotificationListener: Cancellation event already processed, skipping: {}", eventKey);
                        return Mono.empty();
                    }

                    ReservationBaseDTO reservationBaseDTO = ReservationBaseDTO.builder()
                            .reservationId(event.reservationId())
                            .passengerName(event.passengerName())
                            .email(event.email())
                            .reason(event.reason())
                            .build();

                    // NOTA: Debería usar Templates.RESERVATION_CANCELLED en lugar de RESERVATION_CONFIRMED
                    return notificationService.createNotification(reservationBaseDTO,
                                    Templates.RESERVATION_CONFIRMED.getCode())
                            .doOnSuccess(v -> log.info("NotificationListener: Notification sent for cancelled reservation: {}",
                                    event.reservationId()))
                            .then()
                            .onErrorResume(ex -> {
                                log.error("NotificationListener: Error processing ReservationCancelled for reservation {}: {}",
                                        event.reservationId(), ex.getMessage(), ex);

                                processedEvents.remove(eventKey);

                                if (ex instanceof NotificationNotSentException) {
                                    return Mono.error(new AmqpRejectAndDontRequeueException(
                                            "NotificationListener: Notification not sent, sending to DLQ", ex));
                                }

                                return Mono.error(ex);
                            });
                }));
    }

}
