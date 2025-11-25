package com.unimag.notification_service.pollers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unimag.notification_service.entities.Outbox;
import com.unimag.notification_service.entities.Payload;
import com.unimag.notification_service.enums.Constants;
import com.unimag.notification_service.enums.StatusEnum;
import com.unimag.notification_service.repositories.OutboxRepository;
import com.unimag.notification_service.senders.NotificationSender;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.jmx.export.notification.UnableToSendNotificationException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;

@Component
@AllArgsConstructor
public class OutboxPoller {
    private final OutboxRepository outboxRepo;
    private final ObjectMapper objectMapper;
    private final ApplicationContext ctx;

    @Value("${notification.poller.page-size:50}")
    private int pageSize;

    @Scheduled(fixedDelayString = "${notification.poller.delay-ms:5000}")
    public void pollAndSend() {
        Pageable p = PageRequest.of(0, pageSize);
        List<Outbox> pending = outboxRepo.findReady(Instant.now(), p);
        for (Outbox o : pending) {
            process(o);
        }
    }

    @Transactional
    protected void process(Outbox o) {
        // optimistic locking + mark as in-flight if needed
        try {
            // parse payload
            Payload payload = objectMapper.readValue(o.getPayload(), Payload.class);
            NotificationSender sender = ctx.getBean(payload.getChannel(), NotificationSender.class);
            try {
                sender.send(payload.getDestination(), payload.getSubject(), payload.getBody(), o.getEventType());
                o.setStatus(StatusEnum.SENT);
                o.setLastAttemptAt(Instant.now());
                outboxRepo.save(o);
            } catch (UnableToSendNotificationException e) {
                handleFailure(o, "unknown-reason");
            }
        } catch (Exception ex) {
            handleFailure(o, ex.getMessage());
        }
    }

    private void handleFailure(Outbox o, String errorMessage) {
        o.setRetries(o.getRetries() + 1);
        o.setLastAttemptAt(Instant.now());
        o.setErrorMessage(errorMessage);
        if (o.getRetries() >= Constants.MAX_RETRIES) {
            o.setStatus(StatusEnum.FAILED);
        } else {
            // exponential backoff
            long backoffSeconds = (long) Math.pow(2, o.getRetries());
            o.setNextAttemptAt(Instant.now().plusSeconds(backoffSeconds * 60)); // minutes
        }
        outboxRepo.save(o);
    }
}
