package com.unimag.passenger_service.services.publisher;

import com.unimag.passenger_service.config.RabbitConfig;
import com.unimag.passenger_service.events.PassengerRatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventPublisherService {

    private final RabbitTemplate rabbitTemplate;

    public void publishPassengerRated(PassengerRatedEvent event) {
        try {
            rabbitTemplate.convertAndSend(
                    RabbitConfig.EXCHANGE,
                    RabbitConfig.RK_PASSENGER_RATED,
                    event
            );
            log.info("Published PassengerRated: passengerId={}, newRating={}",
                    event.passengerId(), event.newRatingAvg());
        } catch (AmqpException e) {
            log.error("Failed to publish PassengerRated event for passenger: {}",
                    event.passengerId(), e);
        }
    }
}