package com.unimag.trip_service.entities;

import com.unimag.trip_service.enums.TripStatus;
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
@Table("trips")
public class Trip {

    @Id
    private String id;

    @Column("driver_id")
    private String driverId;

    private String origin;

    private String destination;

    @Column("start_time")
    private LocalDateTime startTime;

    @Column("seats_total")
    private int seatsTotal;

    @Column("seats_available")
    private int seatsAvailable;

    private Double price;

    @Column("status")
    private TripStatus status;
}
