package com.unimag.payment_service.listener;

import com.unimag.payment_service.config.RabbitConfig;
import com.unimag.payment_service.events.ReservationRequestedEvent;
import com.unimag.payment_service.services.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentListener {

    private final PaymentService paymentService;
    private final Set<String> processedEvents = ConcurrentHashMap.newKeySet();

    /**
     * Escucha eventos ReservationRequested desde su PROPIA cola
     */
    @RabbitListener(queues = RabbitConfig.QUEUE_PAYMENT_AUTHORIZED)
    public void onReservationRequested(ReservationRequestedEvent event) {
        log.info("PaymentListener: received ReservationRequested: reservationId={}", event.reservationId());

        // Idempotencia: evitar procesar el mismo evento múltiples veces
        String eventKey = "reservation-requested-" + event.reservationId();
        if (!processedEvents.add(eventKey)) {
            log.warn("PaymentListener: Event already processed, skipping: {}", eventKey);
            return; // ACK sin procesar
        }

        try {
            paymentService.processReservationRequested(event);
            log.info("PaymentListener: Successfully processed ReservationRequested: {}", event.reservationId());
        } catch (Exception ex) {
            log.error("PaymentListener: Error processing ReservationRequested for reservation {}: {}",
                    event.reservationId(), ex.getMessage(), ex);

            processedEvents.remove(eventKey); // Permitir reintento

            // Rechazar sin reencolar después de ciertos errores
//            if (ex instanceof ReservationNotFoundException) {
//                throw new AmqpRejectAndDontRequeueException(
//                        "Reservation not found, sending to DLQ", ex);
//            }

            // Otros errores: reintentar
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
        log.info("PaymentListener: Cleaned processed events cache: {} events removed", sizeBefore);
    }
}
