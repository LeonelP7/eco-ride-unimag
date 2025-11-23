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

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationListener {
    private final NotificationService notificationService;
    private final Set<String> processedEvents = ConcurrentHashMap.newKeySet();

    @RabbitListener(queues = RabbitConfig.QUEUE_RESERVATION_CONFIRMED)
    public void onReservationConfirmed(ReservationConfirmedEvent event){
        log.info("NotificationListener: received ReservationConfirmed: reservationId={}", event.reservationId());

        ReservationBaseDTO reservationBaseDTO = ReservationBaseDTO.builder().reservationId(event.reservationId())
                .passengerName(event.passengerName()).email(event.email()).build();

        String eventKey = "reservation-confirmed-" + event.reservationId();
        if (!processedEvents.add(eventKey)) {
            log.warn("NotificationListener: Event already processed, skipping: {}", eventKey);
            return;
        }
        try {
            notificationService.createNotification(reservationBaseDTO, Templates.RESERVATION_CONFIRMED.getCode());
            log.info("NotificationListener: Successfully processed ReservationConfirmed: {}", event.reservationId());
        }catch (Exception ex){
            log.error("NotificationListener: Error processing ReservationConfirmed for reservation {}: {}",
                    event.reservationId(), ex.getMessage(), ex);

            processedEvents.remove(eventKey); // Permitir reintento

            // Rechazar sin reencolar después de ciertos errores
            if (ex instanceof NotificationNotSentException) {
                throw new AmqpRejectAndDontRequeueException(
                        "NotificationListener: Notification not sent, sending to DLQ", ex);
            }

            // Otros errores: reintentar
            throw ex;
        }
    }

    @RabbitListener(queues = RabbitConfig.QUEUE_RESERVATION_CANCELLED)
    public void onReservationFailed(ReservationCancelledEvent event) {
        log.info("NotificationListener: received ReservationCancelled: reservationId={}", event.reservationId());

        ReservationBaseDTO reservationBaseDTO = ReservationBaseDTO.builder().reservationId(event.reservationId())
                .passengerName(event.passengerName()).email(event.email()).reason(event.reason()).build();

        String eventKey = "reservation-cancelled-" + event.reservationId();
        if (!processedEvents.add(eventKey)) {
            log.warn("NotificationListener: Cancelletion event already processed, skipping: {}", eventKey);
            return;
        }

        try {
            notificationService.createNotification(reservationBaseDTO, Templates.RESERVATION_CONFIRMED.getCode());
            log.info("NotificationListener: Successfully processed ReservationCancelled: {}", event.reservationId());
        }catch (Exception ex){
            log.error("NotificationListener: Error processing ReservationCancelled for reservation {}: {}",
                    event.reservationId(), ex.getMessage(), ex);

            processedEvents.remove(eventKey); // Permitir reintento

            // Rechazar sin reencolar después de ciertos errores
            if (ex instanceof NotificationNotSentException) {
                throw new AmqpRejectAndDontRequeueException(
                        "NotificationListener: Notification not sent, sending to DLQ", ex);
            }

            // Otros errores: reintentar
            throw ex;
        }
    }

}
