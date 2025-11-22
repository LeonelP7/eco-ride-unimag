package com.unimag.trip_service.entities;

import com.unimag.trip_service.enums.TripStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
@Entity(name = "trips")
public class Trip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    @Column(name = "driver_id")
    private String driverId;
    private String origin;
    private String destination;
    @Column(name = "start_time")
    private LocalDateTime startTime;
    @Column(name = "seats_total")
    private int seatsTotal;
    @Column(name = "seats_available")
    private int seatsAvailable;
    private Double price;
    @Enumerated(EnumType.STRING)
    private TripStatus status;
    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reservation> reservations = new ArrayList<>();
}
