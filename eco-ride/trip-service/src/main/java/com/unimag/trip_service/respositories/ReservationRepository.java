package com.unimag.trip_service.respositories;

import com.unimag.trip_service.entities.Reservation;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface ReservationRepository extends R2dbcRepository<Reservation, String> {
}
