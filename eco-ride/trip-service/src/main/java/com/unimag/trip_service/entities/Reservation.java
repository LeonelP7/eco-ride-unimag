package com.unimag.trip_service.entities;

import com.unimag.trip_service.enums.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("reservations")
public class Reservation {

    @Id
    private String id;

    @Column("trip_id")
    private String tripId;

    @Column("passenger_id")
    private String passengerId;

    @Column("status")
    private ReservationStatus status;

    @Column("created_at")
    private LocalDateTime createdAt;
}
