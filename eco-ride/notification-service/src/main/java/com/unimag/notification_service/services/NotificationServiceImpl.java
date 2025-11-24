package com.unimag.notification_service.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.unimag.notification_service.dtos.ReservationBaseDTO;
import com.unimag.notification_service.entities.Outbox;

import com.unimag.notification_service.enums.Constants;
import com.unimag.notification_service.enums.StatusEnum;
import com.unimag.notification_service.enums.Templates;
import com.unimag.notification_service.exceptions.EventAlreadyPublishedException;
import com.unimag.notification_service.exceptions.TemplateNotFoundException;
import com.unimag.notification_service.renders.TemplateRenderer;
import com.unimag.notification_service.repositories.OutboxRepository;
import com.unimag.notification_service.repositories.TemplateRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;


@AllArgsConstructor
@Service
public class NotificationServiceImpl implements NotificationService {

    private final OutboxRepository outboxRepository;
    private final TemplateRepository templateRepository;
    private final TemplateRenderer renderer;
    private final ObjectMapper objectMapper;


    @Override
    public Mono<String> createNotification(ReservationBaseDTO event, String code) {
        // Primero comprobamos si ya existe un outbox para esta reserva
        return outboxRepository.findByReservationId(event.reservationId())
                .flatMap(o -> Mono.<String>error(new EventAlreadyPublishedException(o.getId())))
                .switchIfEmpty(
                        templateRepository.findByCode(code)
                                .switchIfEmpty(Mono.error(new TemplateNotFoundException("Template not found.")))
                                .flatMap(tpl -> {
                                    Map<String, Object> vars = new HashMap<>();
                                    String eventType = "";
                                    if (code.equals(Templates.RESERVATION_CONFIRMED.getCode())) {
                                        vars.put("passengerName", event.passengerName());
                                        vars.put("reservationId", event.reservationId());
                                        eventType = Constants.RESERVATION_CONFIRMED;
                                    }
                                    if (code.equals(Templates.RESERVATION_CANCELLED.getCode())){
                                        vars.put("passengerName", event.passengerName());
                                        vars.put("reservationId", event.reservationId());
                                        vars.put("reason", event.reason());
                                        eventType = Constants.RESERVATION_CANCELLED;
                                    }

                                    String body = renderer.render(tpl.getBody(), vars);

                                    return createOutbox(event.email(), tpl.getChannel(), tpl.getSubject(), body, eventType);
                                })
                );
    }

    private Mono<String> createOutbox(String email, String channel, String subject, String body, String eventType) {
        Map<String, Object> payload = Map.of(
                "destination", email,
                "channel", channel,
                "subject", subject,
                "body", body
        );

        Outbox o = new Outbox();
        o.setEventType(eventType);
        o.setPayload(serialize(payload));
        o.setStatus(StatusEnum.PENDING);
        o.setNextAttemptAt(Instant.now());
        return outboxRepository.save(o).map(Outbox::getId);
    }



    private String serialize(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
