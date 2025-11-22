package com.unimag.trip_service.exceptions.notfound;

public class ReservationNotFoundException extends ResourceNotFoundException {
    public ReservationNotFoundException(String message) {
        super(message);
    }
}
