package com.unimag.passenger_service.exceptions.conflict;

import com.unimag.passenger_service.exceptions.ResourceConflictException;

public class PassengerAlreadyExistsException extends ResourceConflictException {
    public PassengerAlreadyExistsException(String message) {
        super(message);
    }
}