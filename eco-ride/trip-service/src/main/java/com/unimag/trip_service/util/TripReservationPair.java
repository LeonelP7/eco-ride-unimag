package com.unimag.trip_service.util;

import com.unimag.trip_service.entities.Reservation;
import com.unimag.trip_service.entities.Trip;

public record TripReservationPair(
        Trip trip, Reservation reservation
) {
}
