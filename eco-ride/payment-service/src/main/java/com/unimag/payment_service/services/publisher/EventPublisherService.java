package com.unimag.payment_service.services.publisher;

import com.unimag.payment_service.config.RabbitConfig;
import com.unimag.payment_service.events.PaymentAuthorizedEvent;
import com.unimag.payment_service.exceptions.EventPublishException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventPublisherService {

    private final RabbitTemplate rabbitTemplate;

    public Mono<Void> publishPaymentAuthorized(PaymentAuthorizedEvent event) {
        return Mono.fromRunnable(() -> {
                    rabbitTemplate.convertAndSend(
                            RabbitConfig.EXCHANGE,
                            RabbitConfig.RK_PAYMENT_AUTHORIZED,
                            event
                    );
                    log.info("Published PaymentAuthorized: reservationId={}", event.reservationId());
                })
                .subscribeOn(Schedulers.boundedElastic())
                .onErrorMap(Exception.class, e -> {
                    log.error("Failed to publish PaymentAuthorized for reservation: {}",
                            event.reservationId(), e);
                    return new EventPublishException("Failed to publish payment authorized event", e);
                })
                .then();
    }
}
