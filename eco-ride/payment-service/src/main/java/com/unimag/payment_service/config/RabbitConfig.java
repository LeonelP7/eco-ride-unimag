package com.unimag.payment_service.config;

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

    // Queues - TripService consume estas dos colas
    public static final String QUEUE_PAYMENT_AUTHORIZED = "payment.authorized.queue";
    public static final String QUEUE_PAYMENT_FAILED = "payment.failed.queue";

    // PaymentService consume esta cola
    public static final String QUEUE_RESERVATION_REQUESTED = "reservation.requested.queue";

    public static final String DLQ = "dlq.reservations";

    // Routing Keys
    public static final String RK_RESERVATION_REQUESTED = "reservation.requested";
    public static final String RK_RESERVATION_CONFIRMED = "reservation.confirmed";
    public static final String RK_RESERVATION_CANCELLED = "reservation.cancelled";
    public static final String RK_PAYMENT_AUTHORIZED = "payment.authorized";
    public static final String RK_PAYMENT_FAILED = "payment.failed";

    @Bean
    public TopicExchange reservationExchange() {
        return new TopicExchange(EXCHANGE, true, false);
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(DLX_EXCHANGE, true, false);
    }

    // Queue para eventos de reservación solicitada (usada por PaymentService)
    @Bean
    public Queue reservationRequestedQueue() {
        return QueueBuilder.durable(QUEUE_RESERVATION_REQUESTED)
                .withArgument("x-message-ttl", 3600000)
                .withArgument("x-dead-letter-exchange", DLX_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", "dlq.reservation")
                .build();
    }

    // Queue para eventos de pago autorizado (usada por TripService)
    @Bean
    public Queue paymentAuthorizedQueue() {
        return QueueBuilder.durable(QUEUE_PAYMENT_AUTHORIZED)
                .withArgument("x-message-ttl", 3600000)
                .withArgument("x-dead-letter-exchange", DLX_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", "dlq.payment")
                .build();
    }

    // Queue para eventos de pago fallido (usada por TripService)
    @Bean
    public Queue paymentFailedQueue() {
        return QueueBuilder.durable(QUEUE_PAYMENT_FAILED)
                .withArgument("x-message-ttl", 3600000)
                .withArgument("x-dead-letter-exchange", DLX_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", "dlq.payment")
                .build();
    }

    @Bean
    public Queue deadLetterQueue() {
        return new Queue(DLQ, true);
    }

    // Bindings
    @Bean
    public Binding bindReservationRequestedQueue(Queue reservationRequestedQueue,
                                                 TopicExchange reservationExchange) {
        return BindingBuilder.bind(reservationRequestedQueue)
                .to(reservationExchange)
                .with(RK_RESERVATION_REQUESTED);
    }

    @Bean
    public Binding bindPaymentAuthorizedQueue(Queue paymentAuthorizedQueue,
                                              TopicExchange reservationExchange) {
        return BindingBuilder.bind(paymentAuthorizedQueue)
                .to(reservationExchange)
                .with(RK_PAYMENT_AUTHORIZED);
    }

    @Bean
    public Binding bindPaymentFailedQueue(Queue paymentFailedQueue,
                                          TopicExchange reservationExchange) {
        return BindingBuilder.bind(paymentFailedQueue)
                .to(reservationExchange)
                .with(RK_PAYMENT_FAILED);
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
