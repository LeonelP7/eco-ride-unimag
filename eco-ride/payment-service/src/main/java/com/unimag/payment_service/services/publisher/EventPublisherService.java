package com.unimag.payment_service.services.publisher;

import com.unimag.payment_service.config.RabbitConfig;
import com.unimag.payment_service.events.PaymentAuthorizedEvent;
import com.unimag.payment_service.exceptions.EventPublishException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventPublisherService {

    private final RabbitTemplate rabbitTemplate;

    public void publishPaymentAuthorized(PaymentAuthorizedEvent event) {
        try {
            rabbitTemplate.convertAndSend(
                    RabbitConfig.EXCHANGE,
                    RabbitConfig.RK_PAYMENT_AUTHORIZED,
                    event
            );
            log.info("Published PaymentAuthorized: reservationId={}", event.reservationId());
        } catch (Exception e) {
            log.error("Failed to publish PaymentAuthorized for reservation: {}", event.reservationId(), e);
            // Aquí podrían implementar reintentos o almacenamiento para envío posterior
            throw new EventPublishException("Failed to publish reservation requested event", e);
        }
    }

}
