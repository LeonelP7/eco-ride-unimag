package com.unimag.trip_service.mappers;

import com.unimag.trip_service.dtos.trip.CreateTripDTO;
import com.unimag.trip_service.dtos.trip.ResponseTripDTO;
import com.unimag.trip_service.entities.Trip;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TripMapper {

    ResponseTripDTO tripToResponse(Trip trip, String driverId);
    CreateTripDTO toCreateDTO(Trip trip);
    Trip createDTOToTrip(CreateTripDTO dto);
}
