package com.unimag.passenger_service.exceptions.notfound;

import com.unimag.passenger_service.exceptions.ResourceNotFoundException;

public class RatingNotFoundException extends ResourceNotFoundException {
    public RatingNotFoundException(String message) {
        super(message);
    }
}