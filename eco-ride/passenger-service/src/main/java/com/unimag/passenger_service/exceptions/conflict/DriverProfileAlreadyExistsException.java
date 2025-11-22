package com.unimag.passenger_service.exceptions.conflict;

import com.unimag.passenger_service.exceptions.ResourceConflictException;

public class DriverProfileAlreadyExistsException extends ResourceConflictException {
    public DriverProfileAlreadyExistsException(String message) {
        super(message);
    }
}