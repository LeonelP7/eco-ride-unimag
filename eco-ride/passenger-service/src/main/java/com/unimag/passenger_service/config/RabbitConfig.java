package com.unimag.passenger_service.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class RabbitConfig {

    // Exchange principal (el mismo que usan los demás servicios)
    public static final String EXCHANGE = "reservation.topic";
    public static final String DLX_EXCHANGE = "dlx.exchange";

    // Queue que PassengerService CONSUME (eventos de TripService)
    public static final String QUEUE_TRIP_COMPLETED = "trip.completed.queue";

    // Queue para Dead Letter
    public static final String DLQ = "dlq.passengers";

    // Routing Keys
    public static final String RK_TRIP_COMPLETED = "trip.completed";
    public static final String RK_PASSENGER_RATED = "passenger.rated";

    @Bean
    public TopicExchange reservationExchange() {
        return new TopicExchange(EXCHANGE, true, false);
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(DLX_EXCHANGE, true, false);
    }

    // Queue para consumir TripCompleted
    @Bean
    public Queue tripCompletedQueue() {
        return QueueBuilder.durable(QUEUE_TRIP_COMPLETED)
                .withArgument("x-message-ttl", 3600000)
                .withArgument("x-dead-letter-exchange", DLX_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", "dlq.trip")
                .build();
    }

    @Bean
    public Queue deadLetterQueue() {
        return new Queue(DLQ, true);
    }

    // Binding: tripCompletedQueue escucha eventos "trip.completed"
    @Bean
    public Binding bindTripCompletedQueue(Queue tripCompletedQueue,
                                          TopicExchange reservationExchange) {
        return BindingBuilder.bind(tripCompletedQueue)
                .to(reservationExchange)
                .with(RK_TRIP_COMPLETED);
    }

    @Bean
    public Binding bindDeadLetterQueue(Queue deadLetterQueue,
                                       DirectExchange deadLetterExchange) {
        return BindingBuilder.bind(deadLetterQueue)
                .to(deadLetterExchange)
                .with("dlq.#");
    }

    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());

        template.setMandatory(true);
        template.setConfirmCallback((correlationData, ack, cause) -> {
            if (!ack) {
                log.error("Message failed to be delivered: {}", cause);
            }
        });

        return template;
    }
}