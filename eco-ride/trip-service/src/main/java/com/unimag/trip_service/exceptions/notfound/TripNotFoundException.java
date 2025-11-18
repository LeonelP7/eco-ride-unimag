package com.unimag.trip_service.exceptions.notfound;

public class TripNotFoundException extends ResourceNotFoundException {
    public TripNotFoundException(String message) {
        super(message);
    }
}
