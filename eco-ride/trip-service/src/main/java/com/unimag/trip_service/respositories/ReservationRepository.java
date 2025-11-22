package com.unimag.trip_service.respositories;

import com.unimag.trip_service.entities.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, String> {
}
