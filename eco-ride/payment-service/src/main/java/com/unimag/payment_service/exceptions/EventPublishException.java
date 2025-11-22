package com.unimag.payment_service.exceptions;

public class EventPublishException extends RuntimeException {
    public EventPublishException(String message) {
        super(message);
    }
    public EventPublishException(String message, Throwable cause) {
        super(message, cause);
    }
}
