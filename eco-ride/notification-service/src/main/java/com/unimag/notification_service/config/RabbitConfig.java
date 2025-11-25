package com.unimag.notification_service.config;

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

    // Exchanges
    public static final String EXCHANGE = "reservation.topic";
    public static final String DLX_EXCHANGE = "dlx.exchange";

    public static final String QUEUE_RESERVATION_CANCELLED = "reservation.cancelled.queue";
    public static final String QUEUE_RESERVATION_CONFIRMED = "reservation.cancelled.queue";

    public static final String DLQ = "dlq.reservations";

    // Routing Keys
    public static final String RK_RESERVATION_CONFIRMED = "reservation.confirmed";
    public static final String RK_RESERVATION_CANCELLED = "reservation.cancelled";

    @Bean
    public TopicExchange reservationExchange() {
        return new TopicExchange(EXCHANGE, true, false);
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(DLX_EXCHANGE, true, false);
    }



    // Queue para eventos de reserva confirmada (usada por PaymentService y notificaciones)
    @Bean
    public Queue reservationConfirmedQueue() {
        return QueueBuilder.durable(QUEUE_RESERVATION_CONFIRMED)
                .withArgument("x-message-ttl", 3600000)
                .withArgument("x-dead-letter-exchange", DLX_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", "dlq.reservation")
                .build();
    }

    // Queue para eventos de reserva cancellada (usada por PaymentService y notificaciones)
    @Bean
    public Queue reservationCancelledQueue() {
        return QueueBuilder.durable(QUEUE_RESERVATION_CANCELLED)
                .withArgument("x-message-ttl", 3600000)
                .withArgument("x-dead-letter-exchange", DLX_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", "dlq.reservation")
                .build();
    }


    @Bean
    public Queue deadLetterQueue() {
        return new Queue(DLQ, true);
    }

    // Bindings
    @Bean
    public Binding bindReservationConfirmedQueue(Queue reservationConfirmedQueue,
                                                 TopicExchange reservationExchange) {
        return BindingBuilder.bind(reservationConfirmedQueue)
                .to(reservationExchange)
                .with(RK_RESERVATION_CONFIRMED);
    }

    @Bean
    public Binding bindReservationCancelledQueue(Queue reservationCancelledQueue,
                                                 TopicExchange reservationExchange) {
        return BindingBuilder.bind(reservationCancelledQueue)
                .to(reservationExchange)
                .with(RK_RESERVATION_CANCELLED);
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

        // Configurar confirmaciones de publicación
        template.setMandatory(true);
        template.setConfirmCallback((correlationData, ack, cause) -> {
            if (!ack) {
                log.error("Message failed to be delivered: {}", cause);
            }
        });

        return template;
    }
}
