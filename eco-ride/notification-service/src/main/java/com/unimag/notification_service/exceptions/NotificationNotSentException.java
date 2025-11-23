package com.unimag.notification_service.exceptions;

public class NotificationNotSentException extends RuntimeException {
    public NotificationNotSentException(String message) {
        super(message);
    }
}
