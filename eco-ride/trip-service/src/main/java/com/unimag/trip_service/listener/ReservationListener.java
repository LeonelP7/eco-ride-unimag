package com.unimag.trip_service.listener;

import com.unimag.trip_service.config.RabbitConfig;
import com.unimag.trip_service.events.PaymentAuthorizedEvent;
import com.unimag.trip_service.events.PaymentFailedEvent;
import com.unimag.trip_service.exceptions.notfound.ReservationNotFoundException;
import com.unimag.trip_service.services.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReservationListener {

    private final ReservationService reservationService;
    private final Set<String> processedEvents = ConcurrentHashMap.newKeySet();

    @RabbitListener(queues = RabbitConfig.QUEUE_PAYMENT_AUTHORIZED)
    public Mono<Void> onPaymentAuthorized(PaymentAuthorizedEvent event) {
        log.info("ReservationListener: received PaymentAuthorized: reservationId={}", event.reservationId());

        String eventKey = "payment-auth-" + event.reservationId();

        // Idempotencia
        if (!processedEvents.add(eventKey)) {
            log.warn("ReservationListener: Event already processed, skipping: {}", eventKey);
            return Mono.empty();
        }

        return reservationService.processPaymentAuthorized(event)
                .doOnSuccess(v -> log.info("ReservationListener: Successfully processed PaymentAuthorized: {}",
                        event.reservationId()))
                .doOnError(ex -> {
                    log.error("ReservationListener: Error processing PaymentAuthorized for reservation {}: {}",
                            event.reservationId(), ex.getMessage(), ex);
                    processedEvents.remove(eventKey);
                })
                .onErrorResume(ex -> {
                    if (ex instanceof ReservationNotFoundException) {
                        return Mono.error(new AmqpRejectAndDontRequeueException(
                                "ReservationListener: Reservation not found, sending to DLQ", ex));
                    }
                    return Mono.error(ex);
                });
    }

    @RabbitListener(queues = RabbitConfig.QUEUE_PAYMENT_FAILED)
    public Mono<Void> onPaymentFailed(PaymentFailedEvent event) {
        log.info("ReservationListener: received PaymentFailed: reservationId={}, reason={}",
                event.reservationId(), event.reason());

        String eventKey = "payment-fail-" + event.reservationId();

        if (!processedEvents.add(eventKey)) {
            log.warn("ReservationListener: Event already processed, skipping: {}", eventKey);
            return Mono.empty();
        }

        return reservationService.processPaymentFailed(event)
                .doOnSuccess(v -> log.info("ReservationListener: Successfully processed PaymentFailed: {}",
                        event.reservationId()))
                .doOnError(ex -> {
                    log.error("ReservationListener: Error processing PaymentFailed for reservation {}: {}",
                            event.reservationId(), ex.getMessage(), ex);
                    processedEvents.remove(eventKey);
                })
                .onErrorResume(ex -> {
                    if (ex instanceof ReservationNotFoundException) {
                        return Mono.error(new AmqpRejectAndDontRequeueException(
                                "ReservationListener: Reservation not found, sending to DLQ", ex));
                    }
                    return Mono.error(ex);
                });
    }

    @Scheduled(fixedRate = 3600000)
    public void cleanProcessedEventsCache() {
        int sizeBefore = processedEvents.size();
        processedEvents.clear();
        log.info("ReservationListener: Cleaned processed events cache: {} events removed", sizeBefore);
    }
}