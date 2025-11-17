package com.unimag.trip_service.services;

import com.unimag.trip_service.dtos.trip.CreateTripDTO;
import com.unimag.trip_service.dtos.trip.ResponseTripDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface TripService {

    ResponseTripDTO saveTrip(CreateTripDTO createTripDTO, String driverId);
    List<ResponseTripDTO> getTrips();
    List<ResponseTripDTO> findByFilters(String origin, String destination, LocalDateTime from, LocalDateTime to);
}
