package com.unimag.payment_service.listener;

import com.unimag.payment_service.config.RabbitConfig;
import com.unimag.payment_service.events.ReservationCancelledEvent;
import com.unimag.payment_service.events.ReservationRequestedEvent;
import com.unimag.payment_service.exceptions.notFound.PaymentNotFoundException;
import com.unimag.payment_service.services.PaymentService;
import jakarta.validation.Valid;
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
public class PaymentListener {

    private final PaymentService paymentService;
    private final Set<String> processedEvents = ConcurrentHashMap.newKeySet();

    @RabbitListener(queues = RabbitConfig.QUEUE_RESERVATION_REQUESTED)
    public Mono<Void> onReservationRequested(@Valid ReservationRequestedEvent event) {
        log.info("PaymentListener: received ReservationRequested: reservationId={}", event.reservationId());

        String eventKey = "reservation-requested-" + event.reservationId();

        // Idempotencia
        if (!processedEvents.add(eventKey)) {
            log.warn("PaymentListener: Event already processed, skipping: {}", eventKey);
            return Mono.empty();
        }

        return paymentService.processReservationRequested(event)
                .doOnSuccess(response -> log.info("PaymentListener: Successfully processed ReservationRequested: {}",
                        event.reservationId()))
                .doOnError(ex -> {
                    log.error("PaymentListener: Error processing ReservationRequested for reservation {}: {}",
                            event.reservationId(), ex.getMessage(), ex);
                    processedEvents.remove(eventKey);
                })
                .then() // Convierte Mono<PaymentResponseDTO> a Mono<Void>
                .onErrorResume(ex -> {
                     if (ex instanceof PaymentNotFoundException) {
                         return Mono.error(new AmqpRejectAndDontRequeueException(
                                 "Payment not found, sending to DLQ", ex));
                     }
                    return Mono.error(ex); // Reintentar para otros errores
                });
    }

    @RabbitListener(queues = RabbitConfig.QUEUE_RESERVATION_CANCELLED)
    public Mono<Void> onReservationCancelled(ReservationCancelledEvent event) {
        log.info("PaymentListener: received ReservationCancelled: reservationId={}", event.reservationId());

        String eventKey = "reservation-cancelled-" + event.reservationId();

        // Idempotencia
        if (!processedEvents.add(eventKey)) {
            log.warn("PaymentListener: Event already processed, skipping: {}", eventKey);
            return Mono.empty();
        }

        return paymentService.processReservationCancelled(event)
                .doOnSuccess(v -> log.info("PaymentListener: Successfully processed ReservationCancelled: {}",
                        event.reservationId()))
                .doOnError(ex -> {
                    log.error("PaymentListener: Error processing ReservationCancelled for reservation {}: {}",
                            event.reservationId(), ex.getMessage(), ex);
                    processedEvents.remove(eventKey);
                })
                .onErrorResume(ex -> {
                     if (ex instanceof PaymentNotFoundException) {
                         return Mono.error(new AmqpRejectAndDontRequeueException(
                                 "Payment not found, sending to DLQ", ex));
                     }
                    return Mono.error(ex);
                });
    }

    /**
     * Limpieza periódica del cache de eventos procesados
     * Evita memory leaks en ambientes de larga duración
     */
    @Scheduled(fixedRate = 3600000) // Cada hora
    public void cleanProcessedEventsCache() {
        int sizeBefore = processedEvents.size();
        processedEvents.clear();
        log.info("PaymentListener: Cleaned processed events cache: {} events removed", sizeBefore);
    }
}
