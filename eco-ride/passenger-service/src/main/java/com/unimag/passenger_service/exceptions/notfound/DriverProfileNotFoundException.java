package com.unimag.passenger_service.exceptions.notfound;

import com.unimag.passenger_service.exceptions.ResourceNotFoundException;

public class DriverProfileNotFoundException extends ResourceNotFoundException {
    public DriverProfileNotFoundException(String message) {
        super(message);
    }
}