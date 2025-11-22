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

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReservationListener {

    private final ReservationService reservationService;
    private final Set<String> processedEvents = ConcurrentHashMap.newKeySet();

    @RabbitListener(queues = RabbitConfig.QUEUE_PAYMENT_AUTHORIZED)
    public void onPaymentAuthorized(PaymentAuthorizedEvent event) {
        log.info("ReservationListener: received PaymentAuthorized: reservationId={}", event.reservationId());

        // Idempotencia: evitar procesar el mismo evento múltiples veces
        String eventKey = "payment-auth-" + event.reservationId();
        if (!processedEvents.add(eventKey)) {
            log.warn("ReservationListener: Event already processed, skipping: {}", eventKey);
            return; // ACK sin procesar
        }

        try {
            reservationService.processPaymentAuthorized(event);
            log.info("ReservationListener: Successfully processed PaymentAuthorized: {}", event.reservationId());
        } catch (Exception ex) {
            log.error("ReservationListener: Error processing PaymentAuthorized for reservation {}: {}",
                    event.reservationId(), ex.getMessage(), ex);

            processedEvents.remove(eventKey); // Permitir reintento

            // Rechazar sin reencolar después de ciertos errores
            if (ex instanceof ReservationNotFoundException) {
                throw new AmqpRejectAndDontRequeueException(
                        "ReservationListener: Reservation not found, sending to DLQ", ex);
            }

            // Otros errores: reintentar
            throw ex;
        }
    }

    @RabbitListener(queues = RabbitConfig.QUEUE_PAYMENT_FAILED)
    public void onPaymentFailed(PaymentFailedEvent event) {
        log.info("ReservationListener: received PaymentFailed: reservationId={}, reason={}",
                event.reservationId(), event.reason());

        // Idempotencia
        String eventKey = "payment-fail-" + event.reservationId();
        if (!processedEvents.add(eventKey)) {
            log.warn("ReservationListener: Event already processed, skipping: {}", eventKey);
            return;
        }

        try {
            reservationService.processPaymentFailed(event);
            log.info("ReservationListener: Successfully processed PaymentFailed: {}", event.reservationId());
        } catch (Exception ex) {
            log.error("ReservationListener: Error processing PaymentFailed for reservation {}: {}",
                    event.reservationId(), ex.getMessage(), ex);

            processedEvents.remove(eventKey);

            if (ex instanceof ReservationNotFoundException) {
                throw new AmqpRejectAndDontRequeueException(
                        "ReservationListener: Reservation not found, sending to DLQ", ex);
            }

            throw ex;
        }
    }

    /**
     * Limpieza periódica del cache de eventos procesados
     * Evita memory leaks en ambientes de larga duración
     */
    @Scheduled(fixedRate = 3600000) // Cada hora
    public void cleanProcessedEventsCache() {
        int sizeBefore = processedEvents.size();
        processedEvents.clear();
        log.info("ReservationListener: Cleaned processed events cache: {} events removed", sizeBefore);
    }
}
