package com.unimag.notification_service.exceptions;

public class EventAlreadyPublishedException extends RuntimeException {
    public EventAlreadyPublishedException(String message) {
        super(message);
    }
}
