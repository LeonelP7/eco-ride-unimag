package com.unimag.trip_service.listener;

import com.unimag.trip_service.events.PaymentAuthorized;
import com.unimag.trip_service.events.PaymentFailedEvent;
import com.unimag.trip_service.services.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReservationListener {

    private final ReservationService reservationService;

    @RabbitListener(queues = "reservations.queue")
    public void onEvent(PaymentAuthorized event) {
        try {
            log.info("Listener received EVENT: " + event.getClass().getSimpleName());
            reservationService.processPaymentAuthorized(event);
        } catch (Exception ex) {
            // Si hay excepción, dejar que Spring reintente según configuración.
            log.error("Error processing EVENT: " + event.getClass().getSimpleName() + " error message: " + ex.getMessage());
            throw ex;
        }
    }

    @RabbitListener(queues = "reservations.queue")
    public void onEvent(PaymentFailedEvent event) {
        try {
            log.info("Listener received EVENT: " + event.getClass().getSimpleName());
            reservationService.processPaymentFailed(event);
        } catch (Exception ex) {
            // Si hay excepción, dejar que Spring reintente según configuración.
            log.error("Error processing EVENT: " + event.getClass().getSimpleName() + " error message: " + ex.getMessage());
            throw ex;
        }
    }
}
