package com.unimag.notification_service.senders;

import org.springframework.jmx.export.notification.UnableToSendNotificationException;

public interface NotificationSender {
    void send(String to, String subject, String body, String payloadMeta) throws UnableToSendNotificationException;

}
