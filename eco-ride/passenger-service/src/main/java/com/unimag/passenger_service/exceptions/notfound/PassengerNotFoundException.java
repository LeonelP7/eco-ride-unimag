package com.unimag.passenger_service.exceptions.notfound;

import com.unimag.passenger_service.exceptions.ResourceNotFoundException;

public class PassengerNotFoundException extends ResourceNotFoundException {
    public PassengerNotFoundException(String message) {
        super(message);
    }
}